package com.osc.sessionservice.exception;

public class InvalidUserIDException extends RuntimeException {
    public InvalidUserIDException(String message) {
        super(message);
    }
}
