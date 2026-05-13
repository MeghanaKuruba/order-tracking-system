package com.ordertracking.restaurant.Exception;

import jakarta.validation.constraints.NotNull;

public class RestaurantAlreadyExistsException extends RuntimeException {
    public RestaurantAlreadyExistsException(String message) {
        super(message);
    }
}
