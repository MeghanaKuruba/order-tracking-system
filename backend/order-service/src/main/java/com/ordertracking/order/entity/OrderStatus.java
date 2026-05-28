package com.ordertracking.order.entity;

public enum OrderStatus {
    CREATED,
    ACCEPTED,
    ASSIGNED,
    PENDING_PAYMENT,
    CONFIRMED,
    REJECTED,
    PREPARING,
    READY_FOR_PICKUP,
    PICKED_UP,
    OUT_FOR_DELIVERY,
    DELIVERED,
    REFUND_INITIATED,
    REFUNDED,
    CANCELLED
}
