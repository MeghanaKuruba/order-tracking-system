package com.ordertracking.verification.exception;

public class VerificationDocumentNotFoundException
        extends RuntimeException {

    public VerificationDocumentNotFoundException(String message) {
        super(message);
    }
}