package com.ordertracking.payment.exception;

public class FailedRazorpayOrderCreation extends RuntimeException{
    public FailedRazorpayOrderCreation(String message) {
        super(message);
    }
}
