package com.ordertracking.cart.exception;

public class RestaurantMismatchException extends RuntimeException {
    public RestaurantMismatchException(String message) {
        super(message);
    }
}
