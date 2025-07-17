package io.github.dariopipa.warehouse.exceptions;

public class ConflictException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ConflictException(String message) {
	super(message);
    }

}
