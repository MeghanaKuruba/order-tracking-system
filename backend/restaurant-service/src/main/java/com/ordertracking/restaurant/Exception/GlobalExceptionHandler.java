package com.ordertracking.restaurant.Exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

     public ResponseEntity<ErrorResponse> handleGenericException(
             Exception ex, HttpServletRequest request) {
         return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), request);
     }

     @ExceptionHandler(RestaurantNotFoundException.class)
     public ResponseEntity<ErrorResponse> handleRestaurantNotFoundException(
             RestaurantNotFoundException ex, HttpServletRequest request) {
         return buildErrorResponse(HttpStatus.NOT_FOUND, "Restaurant Not Found", ex.getMessage(), request);
     }

     @ExceptionHandler(RestaurantClosedException.class)
     public ResponseEntity<ErrorResponse> handleRestaurantClosedException(
             RestaurantClosedException ex, HttpServletRequest request) {
         return buildErrorResponse(HttpStatus.BAD_REQUEST, "Restaurant Closed", ex.getMessage(), request);
     }

     @ExceptionHandler(RestaurantAlreadyExistsException.class)
     public ResponseEntity<ErrorResponse> handleRestaurantAlreadyExistsException(
             RestaurantAlreadyExistsException ex, HttpServletRequest request) {
         return buildErrorResponse(HttpStatus.CONFLICT, "Restaurant Already Exists", ex.getMessage(), request);
     }
     @ExceptionHandler(MenuItemAlreadyExistsException.class)
     public ResponseEntity<ErrorResponse> handleMenuItemAlreadyExistsException(
             MenuItemAlreadyExistsException ex, HttpServletRequest request) {
         return buildErrorResponse(HttpStatus.CONFLICT, "Menu Item Already Exists", ex.getMessage(), request);
     }

     @ExceptionHandler(MenuItemNotFoundException.class)
     public ResponseEntity<ErrorResponse> handleMenuItemNotFoundException(
             MenuItemNotFoundException ex, HttpServletRequest request) {
         return buildErrorResponse(HttpStatus.NOT_FOUND, "Menu Item Not Found", ex.getMessage(), request);
     }

     @ExceptionHandler(NoChangesFoundException.class)
     public ResponseEntity<ErrorResponse> handleNoChangesFoundException(
             NoChangesFoundException ex, HttpServletRequest request) {
         return buildErrorResponse(HttpStatus.BAD_REQUEST, "No Changes Found", ex.getMessage(), request);
     }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status, String error, String message, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                error,
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}
