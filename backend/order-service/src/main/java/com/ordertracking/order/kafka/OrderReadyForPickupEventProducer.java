package com.ordertracking.order.kafka;

import com.ordertracking.order.dto.OrderReadyForPickupEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class OrderReadyForPickupEventProducer {

        private final KafkaTemplate<String, String> kafkaTemplate;

        private final ObjectMapper objectMapper;

        public void sendOrderReadyForPickupEvent(OrderReadyForPickupEvent event) {
            try {
                String jsonMessage = objectMapper.writeValueAsString(event);
                kafkaTemplate.send("order-ready-for-pickup", jsonMessage);
            } catch (Exception e) {
                throw new RuntimeException("Error publishing order ready for pickup event", e);
            }
        }
}
