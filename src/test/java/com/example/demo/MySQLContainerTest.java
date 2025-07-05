package com.example.demo;

import com.example.demo.config.MySQLTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(properties = { "spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=none",
		"spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver",
		"logging.level.org.springframework.jdbc=DEBUG" })
class MySQLContainerTest extends MySQLTestContainer {

	@Test
	void testMySQLContainerIsRunning() {
		System.out.println("[DEBUG_LOG] Starting MySQLContainerTest");
		System.out.println("[DEBUG_LOG] MySQL container running: " + mysqlContainer.isRunning());
		System.out.println("[DEBUG_LOG] MySQL container JDBC URL: " + mysqlContainer.getJdbcUrl());

		assertTrue(mysqlContainer.isRunning(), "MySQL container should be running");

		try {
			// Try to create a simple test table
			System.out.println("[DEBUG_LOG] Creating test table");
			mysqlContainer.execInContainer("mysql", "-utest", "-ptest", "testdb", "-e",
					"CREATE TABLE IF NOT EXISTS test_table (id INT)");
			System.out.println("[DEBUG_LOG] Test table created successfully");
		}
		catch (Exception e) {
			System.out.println("[DEBUG_LOG] Exception occurred: " + e.getMessage());
			throw new RuntimeException("Failed to create test table", e);
		}
	}

}