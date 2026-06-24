package com.charter.rewards.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.charter.rewards.model.dto.ErrorResponse;

class GlobalExceptionHandlerTest {

	private GlobalExceptionHandler exceptionHandler;

	@BeforeEach
	void setUp() {
		exceptionHandler = new GlobalExceptionHandler();
	}

	@Test
	void handleMissingParams_Returns400() {
		MissingServletRequestParameterException ex = new MissingServletRequestParameterException("startDate", "String");
		ResponseEntity<ErrorResponse> response = exceptionHandler.handleMissingParams(ex);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("BAD_REQUEST_002", response.getBody().getErrorCode());
		assertEquals("Missing required parameter: 'startDate'", response.getBody().getErrorMessage());
	}

	@Test
	void handleTypeMismatch_Returns400() {
		MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
		when(ex.getName()).thenReturn("startDate");
		when(ex.getValue()).thenReturn("invalid-date-format");

		ResponseEntity<ErrorResponse> response = exceptionHandler.handleTypeMismatch(ex);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("BAD_REQUEST_003", response.getBody().getErrorCode());
		assertEquals("Invalid value provided for parameter 'startDate'. Please ensure dates are in YYYY-MM-DD format.",
				response.getBody().getErrorMessage());
	}

	@Test
	void handleDateValidationException_Returns400() {
		DateValidationException ex = new DateValidationException("Start date cannot fall after end date.");
		ResponseEntity<ErrorResponse> response = exceptionHandler.handleDateValidationException(ex);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("BAD_REQUEST_001", response.getBody().getErrorCode());
		assertEquals("Start date cannot fall after end date.", response.getBody().getErrorMessage());
	}

	@Test
	void handleTransactionNotFoundException_Returns404() {
		TransactionNotFoundException ex = new TransactionNotFoundException("No transactions found.");
		ResponseEntity<ErrorResponse> response = exceptionHandler.handleTransactionNotFoundException(ex);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("NOT_FOUND_001", response.getBody().getErrorCode());
		assertEquals("No transactions found.", response.getBody().getErrorMessage());
	}

	@Test
	void handleGeneralException_Returns500() {
		Exception ex = new Exception("Database connection failed");
		ResponseEntity<ErrorResponse> response = exceptionHandler.handleGeneralException(ex);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("SERVER_ERROR_500", response.getBody().getErrorCode());
		assertEquals("An unexpected processing error occurred.", response.getBody().getErrorMessage());
	}
}