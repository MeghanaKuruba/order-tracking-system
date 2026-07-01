package com.ordertracking.payment.exception;

import com.ordertracking.payment.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePaymentNotFoundException(PaymentNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Payment Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(PaymentVerificationException.class)
    public ResponseEntity<ErrorResponse> handlePaymentVerificationException(PaymentVerificationException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Payment Verification Exception", ex.getMessage(), request);
    }

    @ExceptionHandler(PaymentAlreadyProcessedException.class)
    public ResponseEntity<ErrorResponse> handlePaymentAlreadyProcessedException(PaymentAlreadyProcessedException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Payment already processed", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidPaymentStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPaymentStateException(InvalidPaymentStateException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Payment State", ex.getMessage(), request);
    }

    @ExceptionHandler(PaymentRetryLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handlePaymentRetryLimitExceededException(PaymentRetryLimitExceededException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.TOO_MANY_REQUESTS, "Maximum payment attempts exceeded", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidWebhookSignatureException.class)
    public ResponseEntity<ErrorResponse> handleInvalidWebhookSignatureException(
            InvalidWebhookSignatureException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid Webhook Signature",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(WebhookProcessingException.class)
    public ResponseEntity<ErrorResponse> handleWebhookProcessingException(
            WebhookProcessingException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Webhook Processing Failed",
                ex.getMessage(),
                request
        );
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
