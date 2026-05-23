package com.ordertracking.restaurant.kafka;

import com.ordertracking.restaurant.dto.OrderConfirmedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-confirmed", groupId = "restaurant-group")
    public void consume(String message){
        OrderConfirmedEvent event = objectMapper.readValue(message, OrderConfirmedEvent.class);
        System.out.println("Received message: " + message);
    }
}
