package io.github.dariopipa.warehouse.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
	public UserAlreadyExistsException(String message) {
		super(message);
	}
}
