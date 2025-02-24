package com.tvo.propertyregister.exception;

public class DontHaveTaxDebtsException extends RuntimeException {
    public DontHaveTaxDebtsException(String message) {
        super(message);
    }
}
