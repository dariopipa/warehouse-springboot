package io.github.dariopipa.warehouse.exceptions;

import java.util.Date;

public class ErrorMessage {
    private int statusCode;
    private Date timestamp;
    private String message;

    public int getStatusCode() {
	return statusCode;
    }

    public void setStatusCode(int statusCode) {
	this.statusCode = statusCode;
    }

    public Date getTimestamp() {
	return timestamp;
    }

    public void setTimestamp(Date timestamp) {
	this.timestamp = timestamp;
    }

    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    public ErrorMessage(int statusCode, Date timestamp, String message) {
	this.statusCode = statusCode;
	this.timestamp = timestamp;
	this.message = message;
    }
}
