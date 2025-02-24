package com.tvo.propertyregister.exception;

public class PropertyNotFoundException extends RuntimeException {
    public PropertyNotFoundException(String message) {
        super(message);
    }
}
