package com.ordertracking.delivery.service.impl;

import com.ordertracking.delivery.dto.DeliveryStatusUpdatedEvent;
import com.ordertracking.delivery.entity.Delivery;
import com.ordertracking.delivery.entity.DeliveryPartner;
import com.ordertracking.delivery.entity.DeliveryStatus;
import com.ordertracking.delivery.exception.DeliveryNotFoundException;
import com.ordertracking.delivery.exception.InvalidDeliveryStateException;
import com.ordertracking.delivery.kafka.DeliveryStatusProducer;
import com.ordertracking.delivery.repository.DeliveryRepository;
import com.ordertracking.delivery.service.DeliveryPartnerService;
import com.ordertracking.delivery.service.DeliveryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;

    private final DeliveryPartnerService deliveryPartnerService;

    private final DeliveryStatusProducer deliveryStatusProducer;

    /**
     * Marks a delivery as delivered. Validates that the delivery exists and is currently out for delivery.
     * Updates the delivery status to DELIVERED, marks the delivery partner as available, and sends a status update event.
     *
     * @param deliveryId The ID of the delivery to mark as delivered
     * @return A success message confirming the order was delivered
     * @throws DeliveryNotFoundException If no delivery is found with the given ID
     * @throws InvalidDeliveryStateException If the delivery is not in OUT_FOR_DELIVERY status
     */
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

    /**
     * Marks the delivery as delivered. Only allowed if the current status is OUT_FOR_DELIVERY.
     * Updates the delivery partner's availability and sends a Kafka event to notify other services.
     *
     * @param deliveryId The ID of the delivery to mark as delivered
     * @return A success message if the operation is successful
     * @throws DeliveryNotFoundException If no delivery is found with the given ID
     * @throws InvalidDeliveryStateException If the delivery is not in OUT_FOR_DELIVERY status
     */
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

    /**
     * Marks the delivery as delivered, updates the delivery partner's availability, and sends a Kafka event to notify other services.
     * @param deliveryId The ID of the delivery to be marked as delivered.
     * @return A success message confirming the order has been delivered.
     * @throws DeliveryNotFoundException If no delivery is found with the given ID.
     * @throws InvalidDeliveryStateException If the delivery is not in the OUT_FOR_DELIVERY status, preventing it from being marked as delivered.
     */
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
                .restaurantId(delivery.getRestaurantId())
                .customerId(delivery.getCustomerId())
                .deliveryStatus(DeliveryStatus.DELIVERED.name())
                .build();
        deliveryStatusProducer.sendDeliveryStatusUpdatedEvent(event);
        deliveryRepository.save(delivery);
        return "Order delivered successfully";
    }


}
