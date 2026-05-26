package com.ordertracking.delivery.service.impl;

import com.ordertracking.delivery.dto.DeliveryStatusUpdatedEvent;
import com.ordertracking.delivery.entity.Delivery;
import com.ordertracking.delivery.entity.DeliveryPartner;
import com.ordertracking.delivery.entity.DeliveryStatus;
import com.ordertracking.delivery.exception.DeliveryNotFoundException;
import com.ordertracking.delivery.exception.InvalidDeliveryStateException;
import com.ordertracking.delivery.kafka.DeliveryStatusProducer;
import com.ordertracking.delivery.repository.DeliveryRepository;
import com.ordertracking.delivery.service.DeliveryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;

    private final DeliveryStatusProducer deliveryStatusProducer;
    @Override
    @Transactional
    public String markPickedUp(Long deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found with ID: " + deliveryId));
        if(delivery.getStatus() != DeliveryStatus.ASSIGNED) {
            throw new InvalidDeliveryStateException("Delivery is not in ASSIGNED status, cannot mark as picked up");
        }
        delivery.setStatus(DeliveryStatus.PICKED_UP);
        DeliveryStatusUpdatedEvent event = DeliveryStatusUpdatedEvent.builder()
                .orderId(delivery.getOrderId())
                .deliveryStatus(DeliveryStatus.PICKED_UP.name())
                .build();
        deliveryStatusProducer.sendDeliveryStatusUpdatedEvent(event);
        deliveryRepository.save(delivery);
        return "Order picked up successfully";
    }

    @Override
    @Transactional
    public String markOutForDelivery(Long deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found with ID: " + deliveryId));
        if(delivery.getStatus() != DeliveryStatus.PICKED_UP) {
            throw new InvalidDeliveryStateException("Delivery is not in PICKED_UP status, cannot mark as out for delivery");
        }
        delivery.setStatus(DeliveryStatus.OUT_FOR_DELIVERY);
        DeliveryStatusUpdatedEvent event = DeliveryStatusUpdatedEvent.builder()
                .orderId(delivery.getOrderId())
                .deliveryStatus(DeliveryStatus.OUT_FOR_DELIVERY.name())
                .build();
        deliveryStatusProducer.sendDeliveryStatusUpdatedEvent(event);
        deliveryRepository.save(delivery);
        return "Order is out for delivery";
    }

    @Override
    @Transactional
    public String markDelivered(Long deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found with ID: " + deliveryId));
        if(delivery.getStatus() != DeliveryStatus.OUT_FOR_DELIVERY) {
            throw new InvalidDeliveryStateException("Delivery is not in OUT_FOR_DELIVERY status, cannot mark as delivered");
        }
        delivery.setStatus(DeliveryStatus.DELIVERED);
        DeliveryPartner partner = delivery.getDeliveryPartner();
        partner.setAvailable(true);
        DeliveryStatusUpdatedEvent event = DeliveryStatusUpdatedEvent.builder()
                .orderId(delivery.getOrderId())
                .deliveryStatus(DeliveryStatus.DELIVERED.name())
                .build();
        deliveryStatusProducer.sendDeliveryStatusUpdatedEvent(event);
        deliveryRepository.save(delivery);
        return "Order delivered successfully";
    }
}
