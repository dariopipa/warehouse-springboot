package io.github.dariopipa.warehouse.exceptions;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<Object> handleEntityNotFoundException(
			EntityNotFoundException ex) {
		ErrorMessage apiError = new ErrorMessage(HttpStatus.NOT_FOUND.value(),
				new Date(), ex.getMessage());

		return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<Object> handleEntityConflictException(
			ConflictException ex) {
		ErrorMessage apiError = new ErrorMessage(HttpStatus.CONFLICT.value(),
				new Date(), ex.getMessage());

		return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGenericException(Exception ex) {
		ErrorMessage apiError = new ErrorMessage(
				HttpStatus.INTERNAL_SERVER_ERROR.value(), new Date(),
				"An unexpected error occurred");

		return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegalArgumentException(
			IllegalArgumentException ex) {
		ErrorMessage apiError = new ErrorMessage(HttpStatus.BAD_REQUEST.value(),
				new Date(), ex.getMessage());

		return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Object> handleConstraintViolation(
			ConstraintViolationException ex) {
		ErrorMessage apiError = new ErrorMessage(HttpStatus.BAD_REQUEST.value(),
				new Date(), ex.getMessage());

		return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
	}

}
