package com.tvo.propertyregister.exception;

public class NoDebtorsInDebtorListException extends RuntimeException {
    public NoDebtorsInDebtorListException(String message) {
        super(message);
    }
}
