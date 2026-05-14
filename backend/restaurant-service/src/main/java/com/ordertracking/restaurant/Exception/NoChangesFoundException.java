package com.ordertracking.restaurant.Exception;

public class NoChangesFoundException extends RuntimeException {
    public NoChangesFoundException(String message) {
        super(message);
    }
}
