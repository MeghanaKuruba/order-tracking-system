package com.ordertracking.cart.exception;

import com.ordertracking.cart.dto.ErrorResponse;
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
    @ExceptionHandler(RestaurantMismatchException.class)
     public ResponseEntity<ErrorResponse> handleRestaurantMismatchException(
             RestaurantMismatchException ex, HttpServletRequest request) {
         return buildErrorResponse(HttpStatus.BAD_REQUEST, "Restaurant Mismatch", ex.getMessage(), request);
     }
     @ExceptionHandler(CartItemNotFoundException.class)
     public ResponseEntity<ErrorResponse> handleCartItemNotFoundException(
             CartItemNotFoundException ex, HttpServletRequest request) {
         return buildErrorResponse(HttpStatus.NOT_FOUND, "Cart Item Not Found", ex.getMessage(), request);
     }
     @ExceptionHandler(InvalidCartItemException.class)
     public ResponseEntity<ErrorResponse> handleInvalidCartItemException(
             InvalidCartItemException ex, HttpServletRequest request) {
         return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Cart Operation", ex.getMessage(), request);
     }
     @ExceptionHandler(CartNotFoundException.class)
     public ResponseEntity<ErrorResponse> handleCartNotFoundException(
             CartNotFoundException ex, HttpServletRequest request) {
         return buildErrorResponse(HttpStatus.NOT_FOUND, "Cart Not Found", ex.getMessage(), request);
     }

     @ExceptionHandler(EmptyCartException.class)
     public ResponseEntity<ErrorResponse> handleEmptyCartException(
           EmptyCartException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Empty Cart", ex.getMessage(), request);
     }

     @ExceptionHandler(OrderServiceException.class)
     public ResponseEntity<ErrorResponse> handleOrderServiceException(
            OrderServiceException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_GATEWAY, "Order Service Error", ex.getMessage(), request);
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
