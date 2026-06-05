package com.ordertracking.tracking.kafka;

import com.ordertracking.tracking.dto.DeliveryAssignedEvent;
import com.ordertracking.tracking.entity.DeliveryLocation;
import com.ordertracking.tracking.repository.TrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryAssignedConsumer {

    private final ObjectMapper objectMapper;

    private final TrackingRepository trackingRepository;
    @KafkaListener(topics = "delivery-assigned", groupId = "tracking-group")
    public void consume(String message) {
        System.out.println("Received message: " + message);
        try {
            DeliveryAssignedEvent event = objectMapper.readValue(message, DeliveryAssignedEvent.class);
            Optional<DeliveryLocation> existingLocation =
                    trackingRepository.findByOrderId(event.getOrderId());

            DeliveryLocation location;

            if (existingLocation.isPresent()) {
                location = existingLocation.get();

                // update existing
                location.setDeliveryPartnerId(event.getDeliveryPartnerId());
                location.setUpdatedAt(LocalDateTime.now());

                System.out.println("Updated tracking for orderId: " + event.getOrderId());

            } else {
                // create new
                location = DeliveryLocation.builder()
                        .orderId(event.getOrderId())
                        .deliveryPartnerId(event.getDeliveryPartnerId())
                        .updatedAt(LocalDateTime.now())
                        .build();

                System.out.println("Tracking record created for orderId: " + event.getOrderId());
            }

            trackingRepository.save(location);

            System.out.println("Tracking record created for orderId: " + event.getOrderId() + " with deliveryPartnerId: " + event.getDeliveryPartnerId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

