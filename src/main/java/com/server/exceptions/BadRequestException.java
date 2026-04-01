package com.server.exceptions;

public class BadRequestException extends RuntimeException {
    private final String field;

    public BadRequestException(String message) {
        super(message);
        this.field = null;
    }

    public BadRequestException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
