package com.ordertracking.delivery.kafka;

import com.ordertracking.delivery.dto.OrderReadyForPickupEvent;
import com.ordertracking.delivery.dto.RestaurantOrderStatusEvent;
import com.ordertracking.delivery.entity.Delivery;
import com.ordertracking.delivery.entity.DeliveryPartner;
import com.ordertracking.delivery.entity.DeliveryStatus;
import com.ordertracking.delivery.exception.DeliveryPartnerNotAvailableException;
import com.ordertracking.delivery.repository.DeliveryPartnerRepository;
import com.ordertracking.delivery.repository.DeliveryRepository;
import com.ordertracking.delivery.service.DeliveryPartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeliveryEventConsumer {

    private final ObjectMapper objectMapper;

    private final DeliveryPartnerService deliveryPartnerService;

    @KafkaListener(topics = "restaurant-order-status", groupId = "delivery-group")
    public void consume(String message) {
        System.out.println("Received message: " + message);
        try {
            System.out.println("Received message: " + message);

            RestaurantOrderStatusEvent event = objectMapper.readValue(message, RestaurantOrderStatusEvent.class);

            if(event.getOrderStatus().equals("READY_FOR_PICKUP")){
                deliveryPartnerService.assignDeliveryPartner(event);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
