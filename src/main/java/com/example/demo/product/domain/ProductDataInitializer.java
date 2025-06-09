package com.example.demo.product.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.List;

/**
 * Initializes sample product data for development.
 */
@Configuration
class ProductDataInitializer {
    
    private static final Logger log = LoggerFactory.getLogger(ProductDataInitializer.class);
    
    /**
     * Creates a CommandLineRunner bean that initializes sample product data.
     * Only active in the "default" profile (not in "prod" or "test").
     */
    @Bean
    @Profile("!prod & !test")
    CommandLineRunner initProductData(ProductRepository productRepository) {
        return args -> {
            log.info("Initializing sample product data");
            
            if (productRepository.count() > 0) {
                log.info("Product database already contains data, skipping initialization");
                return;
            }
            
            List<Product> products = List.of(
                new Product(
                    "Smartphone X", 
                    "Latest smartphone with advanced features and high-resolution camera", 
                    new BigDecimal("999.99"), 
                    "Electronics", 
                    "https://example.com/images/smartphone-x.jpg", 
                    true
                ),
                new Product(
                    "Laptop Pro", 
                    "Powerful laptop for professionals with high performance and long battery life", 
                    new BigDecimal("1499.99"), 
                    "Electronics", 
                    "https://example.com/images/laptop-pro.jpg", 
                    true
                ),
                new Product(
                    "Wireless Headphones", 
                    "Premium noise-cancelling wireless headphones with crystal clear sound", 
                    new BigDecimal("249.99"), 
                    "Electronics", 
                    "https://example.com/images/wireless-headphones.jpg", 
                    true
                ),
                new Product(
                    "Smart Watch", 
                    "Fitness tracker and smartwatch with heart rate monitor and GPS", 
                    new BigDecimal("199.99"), 
                    "Wearables", 
                    "https://example.com/images/smart-watch.jpg", 
                    true
                ),
                new Product(
                    "Coffee Maker", 
                    "Programmable coffee maker with built-in grinder and thermal carafe", 
                    new BigDecimal("129.99"), 
                    "Home Appliances", 
                    "https://example.com/images/coffee-maker.jpg", 
                    true
                ),
                new Product(
                    "Bluetooth Speaker", 
                    "Portable waterproof Bluetooth speaker with 20-hour battery life", 
                    new BigDecimal("89.99"), 
                    "Electronics", 
                    "https://example.com/images/bluetooth-speaker.jpg", 
                    true
                ),
                new Product(
                    "Fitness Tracker", 
                    "Slim fitness band with sleep tracking and water resistance", 
                    new BigDecimal("79.99"), 
                    "Wearables", 
                    "https://example.com/images/fitness-tracker.jpg", 
                    true
                ),
                new Product(
                    "Digital Camera", 
                    "Professional DSLR camera with 4K video recording and interchangeable lenses", 
                    new BigDecimal("899.99"), 
                    "Electronics", 
                    "https://example.com/images/digital-camera.jpg", 
                    true
                ),
                new Product(
                    "Gaming Console", 
                    "Next-generation gaming console with 1TB storage and 4K gaming", 
                    new BigDecimal("499.99"), 
                    "Gaming", 
                    "https://example.com/images/gaming-console.jpg", 
                    true
                ),
                new Product(
                    "Wireless Earbuds", 
                    "True wireless earbuds with touch controls and charging case", 
                    new BigDecimal("149.99"), 
                    "Electronics", 
                    "https://example.com/images/wireless-earbuds.jpg", 
                    true
                )
            );
            
            productRepository.saveAll(products);
            log.info("Sample product data initialized with {} products", products.size());
        };
    }
}