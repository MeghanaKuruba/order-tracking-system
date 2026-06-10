package com.ordertracking.restaurant.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordertracking.restaurant.dto.OrderConfirmedEvent;
import com.ordertracking.restaurant.dto.RestaurantOrderStatusEvent;
import com.ordertracking.restaurant.entity.OrderStatus;
import com.ordertracking.restaurant.entity.RestaurantOrder;
import com.ordertracking.restaurant.kafka.producer.RestaurantOrderStatusProducer;
import com.ordertracking.restaurant.repository.RestaurantOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ObjectMapper objectMapper;

    private final RestaurantOrderRepository restaurantOrderRepository;

    private final RestaurantOrderStatusProducer restaurantOrderStatusProducer;

    @KafkaListener(topics = "order-confirmed", groupId = "restaurant-group")
    public void consume(String message){
        System.out.println("Received order confirmed event: " + message);
        try {
            OrderConfirmedEvent event = objectMapper.readValue(message, OrderConfirmedEvent.class);
            System.out.println("Processing order for restaurant ID: " + event.getRestaurantId() + ", Order ID: " + event.getOrderId());
            System.out.println("Event: " + event);

            RestaurantOrder order = RestaurantOrder.builder()
                    .orderId(event.getOrderId())
                    .restaurantId(event.getRestaurantId())
                    .customerId(event.getCustomerId())
                    .status(OrderStatus.PREPARING)
                    .totalAmount(event.getTotalAmount())
                    .build();
            System.out.println("Created restaurant order: " + order);

            restaurantOrderRepository.save(order);
            System.out.println("Saved restaurant order with ID: " + order.getOrderId());

            RestaurantOrderStatusEvent statusEvent = RestaurantOrderStatusEvent.builder()
                    .orderId(event.getOrderId())
                    .orderStatus("PREPARING")
                    .build();
            restaurantOrderStatusProducer.sendRestaurantOrderStatusEvent(statusEvent);
        } catch (Exception e) {
            e.printStackTrace();
             System.out.println("Failed to process order confirmed event: " + e.getMessage());
        }
    }
}
