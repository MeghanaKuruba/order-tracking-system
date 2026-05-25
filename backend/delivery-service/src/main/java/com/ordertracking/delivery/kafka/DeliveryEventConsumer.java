package com.ordertracking.delivery.kafka;

import com.ordertracking.delivery.dto.OrderReadyForPickupEvent;
import com.ordertracking.delivery.entity.Delivery;
import com.ordertracking.delivery.entity.DeliveryPartner;
import com.ordertracking.delivery.entity.DeliveryStatus;
import com.ordertracking.delivery.repository.DeliveryPartnerRepository;
import com.ordertracking.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeliveryEventConsumer {

    private final ObjectMapper objectMapper;

    private final DeliveryRepository deliveryRepository;

    private final DeliveryPartnerRepository deliveryPartnerRepository;

    @KafkaListener(topics = "order-ready-for-pickup", groupId = "delivery-group")
    public void consume(String message) {
        System.out.println("Received message: " + message);
        try {
            OrderReadyForPickupEvent event = objectMapper.readValue(message, OrderReadyForPickupEvent.class);
            DeliveryPartner partner = deliveryPartnerRepository.findAll()
                    .stream()
                    .filter(p -> p.getActive() && p.getAvailable())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No delivery partner available"));
            partner.setAvailable(false);
            deliveryPartnerRepository.save(partner);

            Delivery delivery = Delivery.builder()
                    .orderId(event.getOrderId())
                    .restaurantId(event.getRestaurantId())
                    .customerId(event.getCustomerId())
                    .status(DeliveryStatus.ASSIGNED)
                    .deliveryPartner(partner)
                    .assignedAt(LocalDateTime.now())
                    .build();
            deliveryRepository.save(delivery);

            System.out.println("Assigned delivery partner " + partner.getName() + " to order " + event.getOrderId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
