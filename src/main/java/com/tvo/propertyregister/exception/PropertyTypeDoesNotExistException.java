package com.tvo.propertyregister.exception;

public class PropertyTypeDoesNotExistException extends RuntimeException{
    public PropertyTypeDoesNotExistException(String message) {
        super(message);
    }
}
