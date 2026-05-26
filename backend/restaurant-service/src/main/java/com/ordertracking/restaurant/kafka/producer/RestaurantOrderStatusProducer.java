package com.ordertracking.restaurant.kafka.producer;

import com.ordertracking.restaurant.dto.RestaurantOrderStatusEvent;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import tools.jackson.databind.ObjectMapper;

@Data
@RequiredArgsConstructor
public class RestaurantOrderStatusProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public void sendRestaurantOrderStatusEvent(RestaurantOrderStatusEvent event) {
        try {
            // Convert the message to JSON string if it's not already a string
            String jsonMessage = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("restaurant-order-status", jsonMessage);
            System.out.println("Sent restaurant order status event for Order ID " + event.getOrderId() + " with status " + event.getOrderStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
