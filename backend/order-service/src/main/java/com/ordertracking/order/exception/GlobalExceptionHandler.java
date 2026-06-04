package com.ordertracking.order.exception;

import com.ordertracking.order.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
     public ResponseEntity<ErrorResponse> handleGenericException(
             Exception ex, HttpServletRequest request) {
         return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), request);
     }

     @ExceptionHandler(InvalidOrderException.class)
     public ResponseEntity<ErrorResponse> handleInvalidOrderException(
             InvalidOrderException ex, HttpServletRequest request) {
         return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Order", ex.getMessage(), request);
     }

     @ExceptionHandler(OrderNotFoundException.class)
     public ResponseEntity<ErrorResponse> handleOrderNotFoundException(
             OrderNotFoundException ex, HttpServletRequest request) {
         return buildErrorResponse(HttpStatus.NOT_FOUND, "Order Not Found", ex.getMessage(), request);
     }

     @ExceptionHandler(MethodArgumentTypeMismatchException.class)
     public ResponseEntity<ErrorResponse> handleEnumError(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
         return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Parameter", ex.getMessage(), request);
     }

     @ExceptionHandler(OrderCancellationNotAllowedException.class)
     public ResponseEntity<ErrorResponse> handleOrderCancellationNotAllowedException(
             OrderCancellationNotAllowedException ex, HttpServletRequest request) {
         return buildErrorResponse(HttpStatus.BAD_REQUEST, "Order Cancellation Not Allowed", ex.getMessage(), request);
     }

     @ExceptionHandler(RestaurantClosedException.class)
     public ResponseEntity<ErrorResponse> handleRestaurantClosedException(
             RestaurantClosedException ex, HttpServletRequest request) {
         return buildErrorResponse(HttpStatus.BAD_REQUEST, "Restaurant Closed", ex.getMessage(), request);
     }

     @ExceptionHandler(RestaurantNotAcceptingOrdersException.class)
     public ResponseEntity<ErrorResponse> handleRestaurantNotAcceptingOrdersException(
             RestaurantNotAcceptingOrdersException ex, HttpServletRequest request) {
         return buildErrorResponse(HttpStatus.BAD_REQUEST, "Restaurant Not Accepting Orders", ex.getMessage(), request);
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
