package com.example.demo.product.api;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Generic record for paginated responses.
 *
 * @param <T> the type of content in the page
 */
public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last
) {
    /**
     * Creates a PageResponse from a Spring Data Page.
     *
     * @param page the Spring Data Page
     * @param <T> the type of content in the page
     * @return a new PageResponse
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
    }
}