package com.ordertracking.order.kafka.producer;

import com.ordertracking.order.dto.OrderConfirmedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class OrderConfirmedEventProducer {

        private final KafkaTemplate<String, String> kafkaTemplate;

        private final ObjectMapper objectMapper;

        public void sendOrderConfirmedEvent(OrderConfirmedEvent event) {
            try {
                String jsonMessage = objectMapper.writeValueAsString(event);
                kafkaTemplate.send("order-confirmed", jsonMessage);
            } catch (Exception e) {
                throw new RuntimeException("Error publishing order confirmed event", e);
            }
        }
}
