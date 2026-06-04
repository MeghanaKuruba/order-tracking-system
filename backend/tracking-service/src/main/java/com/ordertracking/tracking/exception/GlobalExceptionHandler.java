package com.ordertracking.tracking.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.ordertracking.tracking.dto.ErrorResponse;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

     public ResponseEntity<ErrorResponse> handleGenericException(
             Exception ex, HttpServletRequest request) {
         return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), request);
     }
     @ExceptionHandler(LocationNotFoundException.class)
     public ResponseEntity<ErrorResponse> handleLocationNotFoundException(
             LocationNotFoundException ex, HttpServletRequest request) {
         return buildErrorResponse(HttpStatus.NOT_FOUND, "Location Not Found", ex.getMessage(), request);
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
