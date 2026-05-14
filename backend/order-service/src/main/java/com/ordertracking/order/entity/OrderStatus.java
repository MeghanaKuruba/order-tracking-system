package com.ordertracking.order.entity;

public enum OrderStatus {
    CREATED,
    ACCEPTED,
    PREPARING,
    READY_FOR_PICKUP,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}
