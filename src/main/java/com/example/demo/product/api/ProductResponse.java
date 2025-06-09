package com.example.demo.product.api;

import com.example.demo.product.domain.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Record representing a product response.
 */
public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    String category,
    String imageUrl,
    boolean available,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    /**
     * Creates a ProductResponse from a Product entity.
     *
     * @param product the product entity
     * @return a new ProductResponse
     */
    public static ProductResponse fromEntity(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getCategory(),
            product.getImageUrl(),
            product.isAvailable(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
}