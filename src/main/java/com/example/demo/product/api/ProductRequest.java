package com.example.demo.product.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Record representing a product creation or update request.
 */
public record ProductRequest(
		@NotBlank(message = "Product name is required") @Size(min = 2, max = 100,
				message = "Product name must be between 2 and 100 characters") String name,

		@NotBlank(message = "Product description is required") @Size(min = 10, max = 1000,
				message = "Product description must be between 10 and 1000 characters") String description,

		@NotNull(message = "Product price is required") @Positive(
				message = "Product price must be positive") BigDecimal price,

		String category,

		String imageUrl,

		Boolean available) {
	/**
	 * Returns a new ProductRequest with default values for optional fields.
	 */
	public ProductRequest {
		// Set default values for optional fields if they are null
		if (category == null) {
			category = "Uncategorized";
		}

		if (imageUrl == null) {
			imageUrl = "";
		}

		if (available == null) {
			available = true;
		}
	}
}