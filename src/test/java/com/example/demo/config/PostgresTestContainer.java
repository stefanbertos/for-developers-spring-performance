package com.example.demo.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for tests that need a PostgreSQL database.
 * Uses Testcontainers to start a PostgreSQL container.
 */
@Testcontainers
public abstract class PostgresTestContainer {

    @Container
    protected static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    static {
        postgresContainer.start();
    }

    /**
     * Dynamically sets the datasource properties to use the PostgreSQL container.
     */
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }
}