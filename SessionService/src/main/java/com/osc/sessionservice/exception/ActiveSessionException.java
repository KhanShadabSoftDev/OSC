package com.osc.sessionservice.exception;

public class ActiveSessionException extends RuntimeException {
    public ActiveSessionException(String message) {
        super(message);
    }
}
