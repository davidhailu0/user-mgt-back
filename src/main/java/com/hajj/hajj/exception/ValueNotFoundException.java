package com.hajj.hajj.exception;

public class ValueNotFoundException extends Exception{
    public ValueNotFoundException() {
        super();
    }

    public ValueNotFoundException(String message) {
        super(message);
    }

    public ValueNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
