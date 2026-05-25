package com.ordertracking.delivery.service;

public interface DeliveryService {
    String markPickedUp(Long deliveryId);
    String markOutForDelivery(Long deliveryId);
    String markDelivered(Long deliveryId);
}
