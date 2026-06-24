package com.charter.rewards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.charter.rewards.model.dto.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex) {
		String errorMessage = "Missing required parameter: '" + ex.getParameterName() + "'";
		log.warn("Bad Request: {}", errorMessage);
		return new ResponseEntity<>(new ErrorResponse("BAD_REQUEST_002", errorMessage), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		String errorMessage = "Invalid value provided for parameter '" + ex.getName()
				+ "'. Please ensure dates are in YYYY-MM-DD format.";
		log.warn("Bad Request: Type mismatch for parameter '{}'. Value '{}' could not be parsed.", ex.getName(),
				ex.getValue());
		return new ResponseEntity<>(new ErrorResponse("BAD_REQUEST_003", errorMessage), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DateValidationException.class)
	public ResponseEntity<ErrorResponse> handleDateValidationException(DateValidationException ex) {
		log.warn("Validation error occurred: {}", ex.getMessage());
		return new ResponseEntity<>(new ErrorResponse("BAD_REQUEST_001", ex.getMessage()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(TransactionNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleTransactionNotFoundException(TransactionNotFoundException ex) {
		log.info("No data found: {}", ex.getMessage());
		// Return 404 for empty results
		return new ResponseEntity<>(new ErrorResponse("NOT_FOUND_001", ex.getMessage()), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
		log.error("Internal processing error system failure: ", ex);
		return new ResponseEntity<>(new ErrorResponse("SERVER_ERROR_500", "An unexpected processing error occurred."),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
}