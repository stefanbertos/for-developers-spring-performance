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
- PostgreSQL 16.0
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
# Build and start the application with PostgreSQL
docker-compose up -d

# Check the logs
docker-compose logs -f app
```

The API will be available at http://localhost:8080

### Running Locally

To run the application locally:

1. Start a PostgreSQL instance:
   ```bash
   docker-compose up -d postgres
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

### Code Coverage

The project is configured to enforce 100% code coverage. To generate a coverage report:

```bash
./gradlew jacocoTestReport
```

The report will be available at `build/reports/jacoco/test/html/index.html`

## Alternative Implementations

A reactive implementation using Spring WebFlux will be available in a separate branch or repository.
