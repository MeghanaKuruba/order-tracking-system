package com.ordertracking.delivery.kafka;

import com.ordertracking.delivery.dto.OrderReadyForPickupEvent;
import com.ordertracking.delivery.dto.RestaurantOrderStatusEvent;
import com.ordertracking.delivery.service.DeliveryPartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderReadyForPickUpConsumer {

    private final ObjectMapper objectMapper;

    private final DeliveryPartnerService deliveryPartnerService;

    @KafkaListener(topics = "order-ready-for-pickup", groupId = "delivery-group")
    public void consume(String message) {
        System.out.println("Received message: " + message);
        try {
            OrderReadyForPickupEvent event = objectMapper.readValue(message, OrderReadyForPickupEvent.class);
            deliveryPartnerService.assignDeliveryPartner(event);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

