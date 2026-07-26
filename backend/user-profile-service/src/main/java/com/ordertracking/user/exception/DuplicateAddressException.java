package com.ordertracking.user.exception;

public class DuplicateAddressException extends RuntimeException {

    public DuplicateAddressException(String message) {
        super(message);
    }
}