package com.trilogyed.retailapiservice.exception;

/**
 * Exception class to handle user requests for quantities
 * that are greater than listed in the database
 */
public class InsufficientQuantityException extends RuntimeException {

    public InsufficientQuantityException() {
    }

    public InsufficientQuantityException(String message) {
        super(message);
    }
}
