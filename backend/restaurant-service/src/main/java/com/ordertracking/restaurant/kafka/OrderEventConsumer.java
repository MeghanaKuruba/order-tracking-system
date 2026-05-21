package com.ordertracking.restaurant.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    @KafkaListener(topics = "order-created", groupId = "restaurant-group")
    public void consume(String message){
        System.out.println("Received message: " + message);
    }
}
