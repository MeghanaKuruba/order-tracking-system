package com.ordertracking.restaurant.kafka.producer;

import com.ordertracking.restaurant.dto.OrderReadyForPickupEvent;
import com.ordertracking.restaurant.dto.RestaurantOrderStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class OrderReadyForPickUpProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public void orderReadyForPickUpEvent(OrderReadyForPickupEvent event) {
        try {
            // Convert the message to JSON string if it's not already a string
            String jsonMessage = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("order-ready-for-pickup", jsonMessage);
            System.out.println("Sent restaurant address for Order ID " + event.getOrderId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
