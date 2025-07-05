package com.example.demo.product.performance;

import com.example.demo.config.MySQLTestContainer;
import com.example.demo.product.api.PageResponse;
import com.example.demo.product.api.ProductRequest;
import com.example.demo.product.api.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Performance test for the Product API. This test can run in two modes: 1. Local mode: -
 * Starts a MySQL container using Testcontainers - Adds 1000 products - Makes 10 GET
 * requests with size 100 to retrieve all products - Measures the execution time - Asserts
 * that the time is under 1 second 2. Production mode: - Connects to a production endpoint
 * - Makes 10 GET requests with size 100 to retrieve products - Measures the execution
 * time - Asserts that the time is under 1 second
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("performance-test")
class ProductPerformanceTest extends MySQLTestContainer {

	private static final int TOTAL_PRODUCTS = 1000;

	private static final int PAGE_SIZE = 100;

	private static final int TOTAL_PAGES = TOTAL_PRODUCTS / PAGE_SIZE;

	@Value("${performance.test.max-execution-time-ms}")
	private long maxExecutionTimeMs;

	@Value("${performance.test.mode}")
	private String testMode;

	@Value("${performance.test.production-endpoint}")
	private String productionEndpoint;

	@Autowired
	private TestRestTemplate restTemplate;

	private String baseUrl;

	@BeforeEach
	void setup() {
		// Set the base URL based on the test mode
		if ("production".equals(testMode)) {
			baseUrl = productionEndpoint;
			System.out.println("Running in production mode against " + baseUrl);
		}
		else {
			baseUrl = ""; // Empty string for local mode (will use the random port)
			System.out.println("Running in local mode");

			// Create 1000 products in parallel for better performance
			System.out.println("Creating " + TOTAL_PRODUCTS + " products...");
			Instant startCreation = Instant.now();

			try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
				List<CompletableFuture<Void>> futures = new ArrayList<>();

				for (int i = 0; i < TOTAL_PRODUCTS; i++) {
					final int productNumber = i;
					CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
						createProduct(productNumber);
					}, executorService);
					futures.add(future);
				}

				// Wait for all products to be created
				CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
			}

			Duration creationTime = Duration.between(startCreation, Instant.now());
			System.out.println("Created " + TOTAL_PRODUCTS + " products in " + creationTime.toMillis() + "ms");
		}
	}

	@Test
	void retrieveAllProductsPerformanceTest() {
		System.out.println("Starting performance test...");
		Instant start = Instant.now();

		// Make 10 GET requests with size 100 to retrieve all products
		for (int page = 0; page < TOTAL_PAGES; page++) {
			String url = baseUrl + "/api/v1/products?page={page}&size={size}";
			if (baseUrl.isEmpty()) {
				url = "/api/v1/products?page={page}&size={size}";
			}

			Instant batchStart = Instant.now();
			ResponseEntity<PageResponse<ProductResponse>> response = restTemplate.exchange(url, HttpMethod.GET, null,
					new ParameterizedTypeReference<PageResponse<ProductResponse>>() {
					}, page, PAGE_SIZE);
			Duration batchDuration = Duration.between(batchStart, Instant.now());
			long batchExecutionTimeMs = batchDuration.toMillis();

			System.out.println("Performance batch completed in " + batchExecutionTimeMs + "ms");

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			PageResponse<ProductResponse> pageResponse = response.getBody();
			assertThat(pageResponse).isNotNull();

			// In production mode, we don't know how many products there are,
			// so we can't assert the exact size
			if (!"production".equals(testMode)) {
				assertThat(pageResponse.content()).hasSize(PAGE_SIZE);
			}

			// Assert that each batch execution time is under the configured threshold
			assertThat(batchExecutionTimeMs).isLessThanOrEqualTo(maxExecutionTimeMs);
			System.out.println("Retrieved page " + page + " with "
					+ (pageResponse != null ? pageResponse.content().size() : 0) + " products");
		}

		Duration totalDuration = Duration.between(start, Instant.now());
		long totalExecutionTimeMs = totalDuration.toMillis();
		System.out.println("Total performance test completed in " + totalExecutionTimeMs + "ms");
	}

	private void createProduct(int index) {
		ProductRequest request = new ProductRequest("Performance Test Product " + index,
				"This is a product created for performance testing with index " + index,
				BigDecimal.valueOf(10 + (index % 90)), // Price between 10 and 99
				"Performance Test Category " + (index % 10), // 10 different categories
				"https://example.com/performance-test-" + index + ".jpg", true);

		String url = baseUrl + "/api/v1/products";
		if (baseUrl.isEmpty()) {
			url = "/api/v1/products";
		}

		ResponseEntity<ProductResponse> response = restTemplate.postForEntity(url, request, ProductResponse.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().name()).isEqualTo(request.name());
	}

}
