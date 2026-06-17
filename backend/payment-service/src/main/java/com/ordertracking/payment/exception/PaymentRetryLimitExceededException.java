package com.ordertracking.payment.exception;

public class PaymentRetryLimitExceededException extends RuntimeException {
    public PaymentRetryLimitExceededException(String message) {
        super(message);
    }
}
