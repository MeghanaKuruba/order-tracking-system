package com.ordertracking.user.exception;


import com.ordertracking.user.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), request);
    }

    // This exception occurs when validation on an argument annotated with @Valid, @NotBlank, @Pattern fails
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation Failed", message, request);
    }

    @ExceptionHandler(DuplicateAddressException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateAddressException(
            DuplicateAddressException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Duplicate Address", ex.getMessage(), request);
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAddressNotFoundException(
            AddressNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Address Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidAddressException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAddressException(
            InvalidAddressException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Address", ex.getMessage(), request);
    }

    @ExceptionHandler(UserProfileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserProfileNotFoundException(
            UserProfileNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "User Profile Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidProfileException.class)
    public ResponseEntity<ErrorResponse> handleInvalidProfileException(
            InvalidProfileException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Profile Information", ex.getMessage(), request);
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

