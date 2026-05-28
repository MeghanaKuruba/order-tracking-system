package com.ordertracking.restaurant.entity;

public enum OrderStatus {
    ACCEPTED,
    PENDING_PAYMENT,
    REJECTED,
    PREPARING,
    READY_FOR_PICKUP,
    PICKED_UP,
    OUT_FOR_DELIVERY,
    DELIVERED,
    ASSIGNED,
    REFUNDED,
    CANCELLED
}
