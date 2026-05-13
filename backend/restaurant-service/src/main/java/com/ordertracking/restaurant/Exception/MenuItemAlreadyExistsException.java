package com.ordertracking.restaurant.Exception;

public class MenuItemAlreadyExistsException extends RuntimeException {
    public MenuItemAlreadyExistsException(String message) {
        super(message);
    }
}
