package com.example.demo.common.error;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

	private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

	@Test
	void handleValidationExceptions_ShouldReturnProblemDetail() {
		// Arrange
		MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
		BindingResult bindingResult = mock(BindingResult.class);
		FieldError fieldError = new FieldError("object", "field", "error message");

		when(exception.getBindingResult()).thenReturn(bindingResult);
		when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

		// Act
		ProblemDetail problemDetail = exceptionHandler.handleValidationExceptions(exception);

		// Assert
		assertThat(problemDetail).isNotNull();
		assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(problemDetail.getTitle()).isEqualTo("Validation Error");
		assertThat(problemDetail.getDetail()).isEqualTo("Validation failed");
		assertThat(problemDetail.getProperties()).containsKey("errors");
		assertThat(problemDetail.getProperties()).containsKey("timestamp");
	}

	@Test
	void handleConstraintViolationException_ShouldReturnProblemDetail() {
		// Arrange
		Set<ConstraintViolation<?>> violations = new HashSet<>();
		ConstraintViolationException exception = new ConstraintViolationException("Constraint violation", violations);

		// Act
		ProblemDetail problemDetail = exceptionHandler.handleConstraintViolationException(exception);

		// Assert
		assertThat(problemDetail).isNotNull();
		assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(problemDetail.getTitle()).isEqualTo("Constraint Violation");
		assertThat(problemDetail.getDetail()).isEqualTo("Constraint violation");
		assertThat(problemDetail.getProperties()).containsKey("timestamp");
	}

	@Test
	void handleEntityNotFoundException_ShouldReturnProblemDetail() {
		// Arrange
		EntityNotFoundException exception = new EntityNotFoundException("Entity not found");

		// Act
		ProblemDetail problemDetail = exceptionHandler.handleEntityNotFoundException(exception);

		// Assert
		assertThat(problemDetail).isNotNull();
		assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
		assertThat(problemDetail.getTitle()).isEqualTo("Resource Not Found");
		assertThat(problemDetail.getDetail()).isEqualTo("Entity not found");
		assertThat(problemDetail.getProperties()).containsKey("timestamp");
	}

	@Test
	void handleIllegalArgumentException_ShouldReturnProblemDetail() {
		// Arrange
		IllegalArgumentException exception = new IllegalArgumentException("Illegal argument");

		// Act
		ProblemDetail problemDetail = exceptionHandler.handleIllegalArgumentException(exception);

		// Assert
		assertThat(problemDetail).isNotNull();
		assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(problemDetail.getTitle()).isEqualTo("Invalid Request");
		assertThat(problemDetail.getDetail()).isEqualTo("Illegal argument");
		assertThat(problemDetail.getProperties()).containsKey("timestamp");
	}

	@Test
	void handleTypeMismatch_ShouldReturnProblemDetail() {
		// Arrange
		MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
		when(exception.getName()).thenReturn("paramName");
		// Use doReturn instead of when().thenReturn() to avoid generic type issues
		doReturn(String.class).when(exception).getRequiredType();

		// Act
		ProblemDetail problemDetail = exceptionHandler.handleTypeMismatch(exception);

		// Assert
		assertThat(problemDetail).isNotNull();
		assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(problemDetail.getTitle()).isEqualTo("Type Mismatch");
		assertThat(problemDetail.getDetail()).isEqualTo("Parameter 'paramName' should be of type 'String'");
		assertThat(problemDetail.getProperties()).containsKey("timestamp");
	}

	@Test
	void handleGenericException_ShouldReturnProblemDetail() {
		// Arrange
		Exception exception = new Exception("Generic exception");

		// Act
		ProblemDetail problemDetail = exceptionHandler.handleGenericException(exception);

		// Assert
		assertThat(problemDetail).isNotNull();
		assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
		assertThat(problemDetail.getTitle()).isEqualTo("Internal Server Error");
		assertThat(problemDetail.getDetail()).isEqualTo("An unexpected error occurred");
		assertThat(problemDetail.getProperties()).containsKey("timestamp");
	}

	@Test
	void handleNoResourceFoundException_ShouldReturnProblemDetail() {
		// Arrange
		org.springframework.web.servlet.resource.NoResourceFoundException exception = 
			new org.springframework.web.servlet.resource.NoResourceFoundException(HttpMethod.GET, "Resource not found");

		// Act
		ProblemDetail problemDetail = exceptionHandler.handleNoResourceFoundException(exception);

		// Assert
		assertThat(problemDetail).isNotNull();
		assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
		assertThat(problemDetail.getTitle()).isEqualTo("Resource Not Found");
		assertThat(problemDetail.getDetail()).isEqualTo("Resource not found");
		assertThat(problemDetail.getProperties()).containsKey("timestamp");
	}

}
