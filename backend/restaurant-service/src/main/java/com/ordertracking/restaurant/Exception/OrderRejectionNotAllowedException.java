package com.ordertracking.restaurant.Exception;

public class OrderRejectionNotAllowedException extends RuntimeException {
    public OrderRejectionNotAllowedException(String message) {
        super(message);
    }
}
