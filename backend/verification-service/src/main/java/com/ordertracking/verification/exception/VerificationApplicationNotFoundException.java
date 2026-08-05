package com.ordertracking.verification.exception;

public class VerificationApplicationNotFoundException
        extends RuntimeException {

    public VerificationApplicationNotFoundException(
            String message) {

        super(message);
    }
}