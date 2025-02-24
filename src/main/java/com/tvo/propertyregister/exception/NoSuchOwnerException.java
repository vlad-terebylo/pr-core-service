package com.tvo.propertyregister.exception;

public class NoSuchOwnerException extends RuntimeException {
    public NoSuchOwnerException(String message) {
        super(message);
    }
}
