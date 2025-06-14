package com.example.demo.common.error;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST controllers. Uses RFC 9457 ProblemDetails format for
 * error responses.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	private static final String PROBLEM_BASE_URL = "https://api.product-catalog.com/problems";
	private static final String TIMESTAMP = "timestamp";

	/**
	 * Handles validation exceptions from @Valid annotations.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		log.warn("Validation error: {}", errors);

		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
		problemDetail.setTitle("Validation Error");
		problemDetail.setType(URI.create(PROBLEM_BASE_URL + "/validation-error"));
		problemDetail.setProperty(TIMESTAMP, Instant.now());
		problemDetail.setProperty("errors", errors);

		return problemDetail;
	}

	/**
	 * Handles constraint violation exceptions.
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	ProblemDetail handleConstraintViolationException(ConstraintViolationException ex) {
		log.warn("Constraint violation: {}", ex.getMessage());

		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
		problemDetail.setTitle("Constraint Violation");
		problemDetail.setType(URI.create(PROBLEM_BASE_URL + "/constraint-violation"));
		problemDetail.setProperty(TIMESTAMP, Instant.now());

		return problemDetail;
	}

	/**
	 * Handles entity not found exceptions.
	 */
	@ExceptionHandler(EntityNotFoundException.class)
	ProblemDetail handleEntityNotFoundException(EntityNotFoundException ex) {
		log.warn("Entity not found: {}", ex.getMessage());

		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
		problemDetail.setTitle("Resource Not Found");
		problemDetail.setType(URI.create(PROBLEM_BASE_URL + "/not-found"));
		problemDetail.setProperty(TIMESTAMP, Instant.now());

		return problemDetail;
	}

	/**
	 * Handles illegal argument exceptions.
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
		log.warn("Illegal argument: {}", ex.getMessage());

		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
		problemDetail.setTitle("Invalid Request");
		problemDetail.setType(URI.create(PROBLEM_BASE_URL + "/invalid-request"));
		problemDetail.setProperty(TIMESTAMP, Instant.now());

		return problemDetail;
	}

	/**
	 * Handles type mismatch exceptions.
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		String message = String.format("Parameter '%s' should be of type '%s'", ex.getName(),
				ex.getRequiredType());
		log.warn("Type mismatch: {}", message);

		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
		problemDetail.setTitle("Type Mismatch");
		problemDetail.setType(URI.create(PROBLEM_BASE_URL + "/type-mismatch"));
		problemDetail.setProperty(TIMESTAMP, Instant.now());

		return problemDetail;
	}

	@ExceptionHandler(NoResourceFoundException.class)
	ProblemDetail handleNoResourceFoundException(NoResourceFoundException ex) {
		log.warn("resource not found: {}", ex.getMessage());

		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
		problemDetail.setTitle("Resource Not Found");
		problemDetail.setType(URI.create(PROBLEM_BASE_URL + "/not-found"));
		problemDetail.setProperty(TIMESTAMP, Instant.now());

		return problemDetail;
	}

	/**
	 * Handles all other exceptions.
	 */
	@ExceptionHandler(Exception.class)
	ProblemDetail handleGenericException(Exception ex) {
		log.error("Unhandled exception", ex);

		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
				"An unexpected error occurred");
		problemDetail.setTitle("Internal Server Error");
		problemDetail.setType(URI.create(PROBLEM_BASE_URL + "/internal-error"));
		problemDetail.setProperty(TIMESTAMP, Instant.now());

		return problemDetail;
	}

}