package com.ordertracking.order.entity;

public enum OrderStatus {
    CREATED,
    ACCEPTED,
    PENDING_PAYMENT,
    CONFIRMED,
    REJECTED,
    PREPARING,
    READY_FOR_PICKUP,
    OUT_FOR_DELIVERY,
    DELIVERED,
    REFUND_INITIATED,
    REFUNDED,
    CANCELLED
}
