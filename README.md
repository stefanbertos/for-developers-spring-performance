# Product Catalog API

A high-performance Spring Boot application showcasing a product catalog service commonly used in e-commerce applications. This implementation uses Java with Virtual Threads and focuses on performance optimization with metrics and profiler tools.

## Features

- RESTful API for product catalog management (CRUD operations)
- PostgreSQL database for data persistence
- Flyway for database schema migrations
- 100% code coverage with JaCoCo
- Docker support for easy deployment
- Comprehensive test suite with Testcontainers
- API documentation with Swagger/OpenAPI

## Technology Stack

- Java 24 (with Virtual Threads)
- Spring Boot 3.5.0
- Mysql 8.0
- Flyway for database migrations
- JaCoCo for code coverage
- Docker and Docker Compose
- Testcontainers for integration testing

## Getting Started

### Prerequisites

- JDK 24 or later
- Docker and Docker Compose
- Gradle 8.5 or later (or use the included Gradle wrapper)

### Running with Docker Compose

The easiest way to run the application is using Docker Compose:

```bash
# Build and start the application with MySQL
docker-compose up -d

# Check the logs
docker-compose logs -f app
```

The API will be available at http://localhost:8080

### Running Locally

To run the application locally:

1. Start a Mysql instance:
   ```bash
   docker-compose up -d mysql
   ```

2. Run the application:
   ```bash
   ./gradlew bootRun
   ```

### API Documentation

Once the application is running, you can access the Swagger UI at:
http://localhost:8080/swagger-ui.html

## Testing

Run the tests with:

```bash
./gradlew test
```

### Performance Testing

The project includes a performance test that:
1. Creates 1000 products in the database
2. Makes 10 GET requests with size 100 to retrieve all products
3. Measures the execution time
4. Asserts that the time is under the configured threshold

#### Running Performance Tests Against Local Environment

By default, the performance test runs against the local environment:

```bash
./gradlew test --tests "com.example.demo.product.performance.ProductPerformanceTest"
```

This will:
- Start a MySQL container using Testcontainers
- Create 1000 products
- Run the performance test
- Assert that each batch of requests completes within the configured threshold (default: 5000ms)

#### Running Performance Tests Against Production Environment

To run the performance test against a production environment:

1. Edit `src/test/resources/application-performance-test.yaml`:
   ```yaml
   performance:
     test:
       mode: production
       production-endpoint: https://your-production-url.com
       max-execution-time-ms: 5000
   ```

2. Run the test:
   ```bash
   ./gradlew test --tests "com.example.demo.product.performance.ProductPerformanceTest"
   ```

When running against a production environment, the test will:
- Skip the product creation step
- Connect to the specified production endpoint
- Run the performance test
- Assert that each batch of requests completes within the configured threshold

### Code Coverage

The project is configured to enforce 100% code coverage. To generate a coverage report:

```bash
./gradlew jacocoTestReport
```

The report will be available at `build/reports/jacoco/test/html/index.html`

## Code Quality

### SonarQube

The project is configured to use SonarQube for code quality analysis. To run SonarQube:

1. Start SonarQube and its database:
   ```bash
   docker-compose up -d sonarqube db
   ```

2. Access SonarQube at http://localhost:9000 (default credentials: admin/admin)

3. Run the analysis:
   ```bash
   ./gradlew sonarqube
   ```

   Note: The project is already configured with SonarQube properties in build.gradle:
   ```groovy
   sonarqube {
       properties {
           property "sonar.projectKey", "product-catalog"
           property "sonar.host.url", "http://localhost:9000"
           property "sonar.token", "sqa_56c9202206133c743190e1ab734cd9af9b719e4d"
       }
   }
   ```

4. View the results in the SonarQube dashboard

## Alternative Implementations

A reactive implementation using Spring WebFlux will be available in a separate branch or repository.

@TODO
Add more dashboards for monitoring application performance
