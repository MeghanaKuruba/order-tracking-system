package com.ordertracking.restaurant.exception;

public class OrderRejectionNotAllowedException extends RuntimeException {
    public OrderRejectionNotAllowedException(String message) {
        super(message);
    }
}
