package com.example.demo.product.api;

import com.example.demo.product.domain.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

	@Mock
	private ProductService productService;

	@InjectMocks
	private ProductController productController;

	private ProductResponse productResponse;

	private ProductRequest productRequest;

	private final LocalDateTime now = LocalDateTime.now();

	@BeforeEach
	void setUp() {
		// Setup test product response
		productResponse = new ProductResponse(1L, "Test Product", "Test Description", new BigDecimal("99.99"),
				new BigDecimal("90.90"), "Test Category", "https://example.com/test.jpg", true, now, now);

		// Setup test product request
		productRequest = new ProductRequest("Test Product", "Test Description", new BigDecimal("99.99"),
				"Test Category", "https://example.com/test.jpg", true);
	}

	@Test
	void getAllProducts_ShouldReturnPageOfProducts() {
		// Arrange
		Page<ProductResponse> productPage = new PageImpl<>(List.of(productResponse));
		when(productService.getAllProducts(any(Pageable.class))).thenReturn(productPage);

		// Act
		ResponseEntity<PageResponse<ProductResponse>> response = productController.getAllProducts(0, 10, "id", "asc");

		// Assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().content()).hasSize(1);
		assertThat(response.getBody().content().get(0).name()).isEqualTo(productResponse.name());
		verify(productService).getAllProducts(any(Pageable.class));
	}

	@Test
	void getProductsByCategory_ShouldReturnPageOfProductsInCategory() {
		// Arrange
		String category = "Test Category";
		Page<ProductResponse> productPage = new PageImpl<>(List.of(productResponse));
		when(productService.getProductsByCategory(eq(category), any(Pageable.class))).thenReturn(productPage);

		// Act
		ResponseEntity<PageResponse<ProductResponse>> response = productController.getProductsByCategory(category, 0,
				10, "id", "asc");

		// Assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().content()).hasSize(1);
		assertThat(response.getBody().content().get(0).category()).isEqualTo(category);
		verify(productService).getProductsByCategory(eq(category), any(Pageable.class));
	}

	@Test
	void searchProductsByName_ShouldReturnPageOfProductsWithNameContaining() {
		// Arrange
		String name = "Test";
		Page<ProductResponse> productPage = new PageImpl<>(List.of(productResponse));
		when(productService.getProductsByName(eq(name), any(Pageable.class))).thenReturn(productPage);

		// Act
		ResponseEntity<PageResponse<ProductResponse>> response = productController.searchProductsByName(name, 0, 10,
				"id", "asc");

		// Assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().content()).hasSize(1);
		assertThat(response.getBody().content().get(0).name()).contains(name);
		verify(productService).getProductsByName(eq(name), any(Pageable.class));
	}

	@Test
	void getProductById_WhenProductExists_ShouldReturnProduct() {
		// Arrange
		Long id = 1L;
		when(productService.getProductById(id)).thenReturn(productResponse);

		// Act
		ResponseEntity<ProductResponse> response = productController.getProductById(id);

		// Assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().id()).isEqualTo(id);
		verify(productService).getProductById(id);
	}

	@Test
	void getProductById_WhenProductDoesNotExist_ShouldReturnNotFound() {
		// Arrange
		Long id = 999L;
		when(productService.getProductById(id))
			.thenThrow(new EntityNotFoundException("Product not found with ID: " + id));

		// Act
		ResponseEntity<ProductResponse> response = productController.getProductById(id);

		// Assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isNull();
		verify(productService).getProductById(id);
	}

	@Test
	void createProduct_WhenNameIsUnique_ShouldCreateProduct() {
		// Arrange
		when(productService.createProduct(productRequest)).thenReturn(productResponse);

		// Act
		ResponseEntity<ProductResponse> response = productController.createProduct(productRequest);

		// Assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().name()).isEqualTo(productRequest.name());
		verify(productService).createProduct(productRequest);
	}

	@Test
	void createProduct_WhenNameExists_ShouldReturnBadRequest() {
		// Arrange
		when(productService.createProduct(productRequest)).thenThrow(
				new IllegalArgumentException("Product with name '" + productRequest.name() + "' already exists"));

		// Act
		ResponseEntity<ProductResponse> response = productController.createProduct(productRequest);

		// Assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).isNull();
		verify(productService).createProduct(productRequest);
	}

	@Test
	void updateProduct_WhenProductExistsAndNameIsUnique_ShouldUpdateProduct() {
		// Arrange
		Long id = 1L;
		when(productService.updateProduct(id, productRequest)).thenReturn(productResponse);

		// Act
		ResponseEntity<ProductResponse> response = productController.updateProduct(id, productRequest);

		// Assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().id()).isEqualTo(id);
		verify(productService).updateProduct(id, productRequest);
	}

	@Test
	void updateProduct_WhenProductDoesNotExist_ShouldReturnNotFound() {
		// Arrange
		Long id = 999L;
		when(productService.updateProduct(id, productRequest))
			.thenThrow(new EntityNotFoundException("Product not found with ID: " + id));

		// Act
		ResponseEntity<ProductResponse> response = productController.updateProduct(id, productRequest);

		// Assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isNull();
		verify(productService).updateProduct(id, productRequest);
	}

	@Test
	void updateProduct_WhenNameExistsForDifferentProduct_ShouldReturnBadRequest() {
		// Arrange
		Long id = 1L;
		when(productService.updateProduct(id, productRequest)).thenThrow(new IllegalArgumentException(
				"Another product with name '" + productRequest.name() + "' already exists"));

		// Act
		ResponseEntity<ProductResponse> response = productController.updateProduct(id, productRequest);

		// Assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).isNull();
		verify(productService).updateProduct(id, productRequest);
	}

	@Test
	void deleteProduct_WhenProductExists_ShouldDeleteProduct() {
		// Arrange
		Long id = 1L;
		doNothing().when(productService).deleteProduct(id);

		// Act
		ResponseEntity<Void> response = productController.deleteProduct(id);

		// Assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(productService).deleteProduct(id);
	}

	@Test
	void deleteProduct_WhenProductDoesNotExist_ShouldReturnNotFound() {
		// Arrange
		Long id = 999L;
		doThrow(new EntityNotFoundException("Product not found with ID: " + id)).when(productService).deleteProduct(id);

		// Act
		ResponseEntity<Void> response = productController.deleteProduct(id);

		// Assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		verify(productService).deleteProduct(id);
	}

}
