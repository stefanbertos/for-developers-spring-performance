package com.example.demo.product.api;

import com.example.demo.product.domain.CurrencyExchangeService;
import com.example.demo.product.domain.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Record representing a product response.
 */
public record ProductResponse(Long id, String name, String description, BigDecimal priceUSD, BigDecimal priceEUR, String category,
		String imageUrl, boolean available, LocalDateTime createdAt, LocalDateTime updatedAt) {
	/**
	 * Creates a ProductResponse from a Product entity.
	 * @param product the product entity
	 * @return a new ProductResponse
	 */
	public static ProductResponse fromEntity(Product product) {
		return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice(),
				null, product.getCategory(), product.getImageUrl(), product.isAvailable(), product.getCreatedAt(),
				product.getUpdatedAt());
	}

	/**
	 * Creates a ProductResponse from a Product entity with currency conversion.
	 * @param product the product entity
	 * @param currencyExchangeService the currency exchange service
	 * @return a new ProductResponse
	 */
	public static ProductResponse fromEntity(Product product, CurrencyExchangeService currencyExchangeService) {
		BigDecimal priceUSD = product.getPrice();
		BigDecimal priceEUR = currencyExchangeService.convertUsdToEur(priceUSD);

		return new ProductResponse(product.getId(), product.getName(), product.getDescription(), priceUSD,
				priceEUR, product.getCategory(), product.getImageUrl(), product.isAvailable(), product.getCreatedAt(),
				product.getUpdatedAt());
	}
}
