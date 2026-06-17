package com.ordertracking.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

public enum PaymentStatus {
    PENDING_PAYMENT,
    SUCCESS,
    FAILED,
    REFUNDED,
    EXPIRED
}
