package com.example.demo.product.api;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRequestTest {

	@Test
	void constructor_WhenAllFieldsProvided_ShouldCreateProductRequest() {
		// Arrange & Act
		ProductRequest request = new ProductRequest("Test Product", "Test Description", new BigDecimal("99.99"),
				"Test Category", "https://example.com/test.jpg", true);

		// Assert
		assertThat(request).isNotNull();
		assertThat(request.name()).isEqualTo("Test Product");
		assertThat(request.description()).isEqualTo("Test Description");
		assertThat(request.price()).isEqualTo(new BigDecimal("99.99"));
		assertThat(request.category()).isEqualTo("Test Category");
		assertThat(request.imageUrl()).isEqualTo("https://example.com/test.jpg");
		assertThat(request.available()).isTrue();
	}

	@Test
	void constructor_WhenOptionalFieldsAreNull_ShouldSetDefaultValues() {
		// Arrange & Act
		ProductRequest request = new ProductRequest("Test Product", "Test Description", new BigDecimal("99.99"), null,
				null, null);

		// Assert
		assertThat(request).isNotNull();
		assertThat(request.name()).isEqualTo("Test Product");
		assertThat(request.description()).isEqualTo("Test Description");
		assertThat(request.price()).isEqualTo(new BigDecimal("99.99"));
		assertThat(request.category()).isEqualTo("Uncategorized");
		assertThat(request.imageUrl()).isEqualTo("");
		assertThat(request.available()).isTrue();
	}

}