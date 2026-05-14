package com.ordertracking.order.exception;

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

     @ExceptionHandler(InvalidOrderException.class)
     public ResponseEntity<ErrorResponse> handleInvalidOrderException(
             InvalidOrderException ex, HttpServletRequest request) {
         return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Order", ex.getMessage(), request);
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
