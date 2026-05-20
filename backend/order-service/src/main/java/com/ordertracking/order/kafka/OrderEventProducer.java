package com.ordertracking.order.kafka;

import com.ordertracking.order.dto.OrderCreatedEvent;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        try {
            // Convert the message to JSON string if it's not already a string
            String jsonMessage = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("order-created", jsonMessage);
        } catch (Exception e) {
            throw new RuntimeException("Error converting event to JSON", e);
        }
    }
}
