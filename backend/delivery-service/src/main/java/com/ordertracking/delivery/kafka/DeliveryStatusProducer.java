package com.ordertracking.delivery.kafka;

import com.ordertracking.delivery.dto.DeliveryStatusUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class DeliveryStatusProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public void sendDeliveryStatusUpdatedEvent(DeliveryStatusUpdatedEvent event) {
        try {
            // Convert the message to JSON string if it's not already a string
            String jsonMessage = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("delivery-status-updated", jsonMessage);
            System.out.println("Sent delivery status update event: " + jsonMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
