package com.example.demo.product.api;

import com.example.demo.product.domain.CurrencyExchangeService;
import com.example.demo.product.domain.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductResponseTest {

	@Test
	void fromEntity_ShouldCreateProductResponseFromProduct() {
		// Arrange
		LocalDateTime now = LocalDateTime.now();
		Product product = mock(Product.class);
		when(product.getId()).thenReturn(1L);
		when(product.getName()).thenReturn("Test Product");
		when(product.getDescription()).thenReturn("Test Description");
		when(product.getPrice()).thenReturn(new BigDecimal("99.99"));
		when(product.getCategory()).thenReturn("Test Category");
		when(product.getImageUrl()).thenReturn("https://example.com/test.jpg");
		when(product.isAvailable()).thenReturn(true);
		when(product.getCreatedAt()).thenReturn(now);
		when(product.getUpdatedAt()).thenReturn(now);

		// Act
		ProductResponse response = ProductResponse.fromEntity(product);

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.name()).isEqualTo("Test Product");
		assertThat(response.description()).isEqualTo("Test Description");
		assertThat(response.priceUSD()).isEqualTo(new BigDecimal("99.99"));
		assertThat(response.priceEUR()).isNull();
		assertThat(response.category()).isEqualTo("Test Category");
		assertThat(response.imageUrl()).isEqualTo("https://example.com/test.jpg");
		assertThat(response.available()).isTrue();
		assertThat(response.createdAt()).isEqualTo(now);
		assertThat(response.updatedAt()).isEqualTo(now);
	}

	@Test
	void fromEntity_WithCurrencyExchangeService_ShouldCreateProductResponseWithBothPrices() {
		// Arrange
		LocalDateTime now = LocalDateTime.now();
		Product product = mock(Product.class);
		when(product.getId()).thenReturn(1L);
		when(product.getName()).thenReturn("Test Product");
		when(product.getDescription()).thenReturn("Test Description");
		when(product.getPrice()).thenReturn(new BigDecimal("99.99"));
		when(product.getCategory()).thenReturn("Test Category");
		when(product.getImageUrl()).thenReturn("https://example.com/test.jpg");
		when(product.isAvailable()).thenReturn(true);
		when(product.getCreatedAt()).thenReturn(now);
		when(product.getUpdatedAt()).thenReturn(now);

		CurrencyExchangeService currencyExchangeService = mock(CurrencyExchangeService.class);
		when(currencyExchangeService.getExchangeRate("USD", "EUR")).thenReturn(new BigDecimal("1.1"));

		// Act
		ProductResponse response = ProductResponse.fromEntity(product, currencyExchangeService);

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.name()).isEqualTo("Test Product");
		assertThat(response.description()).isEqualTo("Test Description");
		assertThat(response.priceUSD()).isEqualTo(new BigDecimal("99.99"));
		assertThat(response.priceEUR())
			.isEqualTo(new BigDecimal("99.99").divide(new BigDecimal("1.1"), 2, java.math.RoundingMode.HALF_UP));
		assertThat(response.category()).isEqualTo("Test Category");
		assertThat(response.imageUrl()).isEqualTo("https://example.com/test.jpg");
		assertThat(response.available()).isTrue();
		assertThat(response.createdAt()).isEqualTo(now);
		assertThat(response.updatedAt()).isEqualTo(now);
	}

}
