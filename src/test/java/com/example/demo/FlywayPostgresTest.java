package com.example.demo;

import com.example.demo.config.PostgresTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.flyway.enabled=true",
    "spring.jpa.hibernate.ddl-auto=validate",
    "spring.datasource.driver-class-name=org.postgresql.Driver",
    "logging.level.org.flywaydb=DEBUG",
    "logging.level.org.springframework.jdbc=DEBUG"
})
class FlywayPostgresTest extends PostgresTestContainer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void flywayMigrationsExecutedSuccessfully() {
        // Query the products table which should be created by Flyway migrations
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM products", Integer.class);

        // The V2__insert_sample_data.sql should insert some sample products
        assertEquals(true, count > 0, "Products table should contain data from Flyway migrations");

        System.out.println("[DEBUG_LOG] Found " + count + " products in the database");
    }
}
