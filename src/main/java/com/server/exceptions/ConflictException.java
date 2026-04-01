package com.server.exceptions;

public class ConflictException extends RuntimeException {
    private final String field;

    public ConflictException(String message) {
        super(message);
        this.field = null;
    }

    public ConflictException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
