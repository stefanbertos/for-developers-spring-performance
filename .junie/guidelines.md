## Core Technologies & versions
* **Java:** Use the latest Long-Term Support (LTS) version of Java
* **Spring Boot:** Always use the latest stable release of Spring Boot (or the latest major stable version available) for new features
* **Build Tools:** Use Gradle as the build tool. 

## Project Structure
* **Packaging:** Strongly prefer a **package-by-feature** structure over package-by-layer. This means grouping all code related to a specific feature or domain concept in the same package.
* Follow the Spring Modulith approach https://spring.io/projects/spring-modulith 

## Lombok Usage
* **Lombok Usage:**
- Dont use Lombok but prefer Java Records

# Spring Boot Guidelines

## 1. Prefer Constructor Injection over Field/Setter Injection
* Declare all the mandatory dependencies as `final` fields and inject them through the constructor.
* Spring will auto-detect if there is only one constructor, no need to add `@Autowired` on the constructor.
* Avoid field/setter injection in production code.

## 2. Prefer package-private over public for Spring components
* Declare Controllers, their request-handling methods, `@Configuration` classes and `@Bean` methods with default (package-private) visibility whenever possible. There's no obligation to make everything `public`.

## 3. Organize Configuration with Typed Properties
* Group application-specific configuration properties with a common prefix in `application.properties` or `.yml`.
* Bind them to `@ConfigurationProperties` classes with validation annotations so that the application will fail fast if the configuration is invalid.
* Prefer environment variables instead of profiles for passing different configuration properties for different environments.

## 4. Define Clear Transaction Boundaries
* Define each Service-layer method as a transactional unit.
* Annotate query-only methods with `@Transactional(readOnly = true)`.
* Annotate data-modifying methods with `@Transactional`.
* Limit the code inside each transaction to the smallest necessary scope.

## 5. Logging
* Use Slf4j with a suitable backend for logging. Prefer Logback

## 6.Service Layer
* Keep business logic in service classes, separate from controllers.

## 7. Validation
* Use Bean Validation (JSR-380) with annotations like `@Valid` and `@NotNull`.

## 8. Profiles:
* Utilize Spring profiles for environment-specific configurations.

## 9. Disable Open Session in View Pattern
* While using Spring Data JPA, disable the Open Session in View filter by setting ` spring.jpa.open-in-view=false` in `application.properties/yml.`

## 10. Separate Web Layer from Persistence Layer
* Don't expose entities directly as responses in controllers.
* Define explicit request and response record (DTO) classes instead.
* Apply Jakarta Validation annotations on your request records to enforce input rules.

## 11. Follow REST API Design Principles
* **Versioned, resource-oriented URLs:** Structure your endpoints as `/api/v{version}/resources` (e.g. `/api/v1/orders`).
* **Consistent patterns for collections and sub-resources:** Keep URL conventions uniform (for example, `/posts` for posts collection and `/posts/{slug}/comments` for comments of a specific post).
* **Explicit HTTP status codes via ResponseEntity:** Use `ResponseEntity<T>` to return the correct status (e.g. 200 OK, 201 Created, 404 Not Found) along with the response body.
* Use pagination for collection resources that may contain an unbounded number of items.
* The JSON payload must use a JSON object as a top-level data structure to allow for future extension.
* Use snake_case or camelCase for JSON property names consistently.

## 12. Centralize Exception Handling
* Define a global handler class annotated with `@ControllerAdvice` (or `@RestControllerAdvice` for REST APIs) using `@ExceptionHandler` methods to handle specific exceptions.
* Return consistent error responses. Consider using the ProblemDetails response format ([RFC 9457](https://www.rfc-editor.org/rfc/rfc9457)).

## 13. Actuator
* Expose only essential actuator endpoints (such as `/health`, `/info`, `/metrics`) without requiring authentication. All the other actuator endpoints must be secured.

## 14. Use Testcontainers for integration tests
* Spin up real services (databases, message brokers, etc.) in your integration tests to mirror production environments.

## 15. Use random port for integration tests
* When writing integration tests, start the application on a random available port to avoid port conflicts by annotating the test class with:

    ```java
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    ```
## 16. Protect sensitive data
* Ensure that no credentials, personal information, or other confidential details ever appear in log output.

## 17. Guard expensive log calls
* When building verbose messages at `DEBUG` or `TRACE` level, especially those involving method calls or complex string concatenations, wrap them in a level check or use suppliers:

```java
if (logger.isDebugEnabled()) {
    logger.debug("Detailed state: {}", computeExpensiveDetails());
}

// using Supplier/Lambda expression
logger.atDebug()
	.setMessage("Detailed state: {}")
	.addArgument(() -> computeExpensiveDetails())
    .log();
```

## 18. HTTP Clients
* **Outgoing HTTP Requests:** Use the Spring Framework 6+ **`RestClient`** for making synchronous or asynchronous HTTP calls
* Add default retry with reasonable retry policy use @Retryable 

## 19. REST Controllers
* **Limit Records Returned:** Don't create controllers with methods where clients can retrieve unlimited number of records. Use pageable option.
* **Documentation:** Add Swagger OpenAPI documentation for all REST controller methods exposed.
* **Response Entities:** Use ResponseEntity to provide full control over the HTTP response.
* **Request Mapping:** Use specific HTTP method annotations (`@GetMapping`, `@PostMapping`, etc.) instead of `@RequestMapping` with method attribute.
* **Path Variables:** Use `@PathVariable` for RESTful resource identifiers and `@RequestParam` for query parameters.
* **Content Negotiation:** Support multiple content types where appropriate (JSON, XML, etc.).

## 20. Java Language Features
* **Data Carriers:** Use Java **records** (record) for immutable data transfer objects (DTOs), value objects, or simple data aggregates.
* **New object construction:** Use Builder pattern wherever possible.
* **Immutability:** Favour immutability of objects where appropriate, especially for DTO's and configuration properties.
* **Optional:** Use Java's Optional for values that might be null, especially in service return types.
* **Stream API:** Leverage Stream API for collection processing as demonstrated in the ImageGenerationService.
* **Functional Interfaces:** Use functional interfaces and lambda expressions for cleaner code.
* **Pattern Matching:** Utilize pattern matching for instanceof (Java 16+) where applicable.
* **Variable Declaration:** Use the Java var variable declaration wherever possible.

## 21. Code coverage
* Unit coverage must be 100%