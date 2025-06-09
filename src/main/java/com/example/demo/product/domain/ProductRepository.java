package com.example.demo.product.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Product entity operations.
 */
@Repository
interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Find products by category.
     *
     * @param category the category to search for
     * @return list of products in the given category
     */
    List<Product> findByCategory(String category);
    
    /**
     * Find products by category with pagination.
     *
     * @param category the category to search for
     * @param pageable pagination information
     * @return page of products in the given category
     */
    Page<Product> findByCategory(String category, Pageable pageable);
    
    /**
     * Find products by availability status.
     *
     * @param available the availability status to search for
     * @param pageable pagination information
     * @return page of products with the given availability status
     */
    Page<Product> findByAvailable(boolean available, Pageable pageable);
    
    /**
     * Find products by name containing the given string (case insensitive).
     *
     * @param name the name substring to search for
     * @param pageable pagination information
     * @return page of products with names containing the given string
     */
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    /**
     * Find a product by its exact name.
     *
     * @param name the exact name to search for
     * @return optional containing the product if found
     */
    Optional<Product> findByNameIgnoreCase(String name);
}