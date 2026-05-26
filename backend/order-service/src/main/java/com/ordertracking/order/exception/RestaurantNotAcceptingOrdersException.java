package com.ordertracking.order.exception;

public class RestaurantNotAcceptingOrdersException extends RuntimeException {
    public RestaurantNotAcceptingOrdersException(String message) {
        super(message);
    }
}
