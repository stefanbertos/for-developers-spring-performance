package com.example.demo;

import com.example.demo.config.MySQLTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(properties = { "spring.flyway.enabled=true", "spring.jpa.hibernate.ddl-auto=none",
		"spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver", "logging.level.org.flywaydb=DEBUG",
		"logging.level.org.springframework.jdbc=DEBUG", "spring.flyway.baseline-on-migrate=true",
		"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect" })
class FlywayMySQLTest extends MySQLTestContainer {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void flywayMigrationsExecutedSuccessfully() {
		System.out.println("[DEBUG_LOG] Starting FlywayMySQLTest");
		System.out.println("[DEBUG_LOG] MySQL container running: " + mysqlContainer.isRunning());
		System.out.println("[DEBUG_LOG] MySQL container JDBC URL: " + mysqlContainer.getJdbcUrl());

		try {
			// Query the products table which should be created by Flyway migrations
			System.out.println("[DEBUG_LOG] Executing SQL query");
			Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products", Integer.class);

			// The V2__insert_sample_data.sql should insert some sample products
			assertEquals(true, count > 0, "Products table should contain data from Flyway migrations");

			System.out.println("[DEBUG_LOG] Found " + count + " products in the database");
		}
		catch (Exception e) {
			System.out.println("[DEBUG_LOG] Exception occurred: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

}
