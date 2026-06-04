package com.ordertracking.restaurant.kafka.consumer;

import com.ordertracking.restaurant.exception.OrderNotFoundException;
import com.ordertracking.restaurant.dto.DeliveryStatusUpdatedEvent;
import com.ordertracking.restaurant.entity.OrderStatus;
import com.ordertracking.restaurant.entity.RestaurantOrder;
import com.ordertracking.restaurant.repository.RestaurantOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class DeliveryStatusConsumer {

    private final ObjectMapper objectMapper;

    private final RestaurantOrderRepository restaurantOrderRepository;

    @KafkaListener(topics = "delivery-status-updated", groupId = "restaurant-group")
    public void consumeDeliveryStatusUpdatedEvent(String message) {
        try {
            DeliveryStatusUpdatedEvent event = objectMapper.readValue(message, DeliveryStatusUpdatedEvent.class);
            RestaurantOrder order = restaurantOrderRepository.findById(event.getOrderId())
                            .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + event.getOrderId()));
            order.setStatus(OrderStatus.valueOf(event.getDeliveryStatus()));
            restaurantOrderRepository.save(order);
            System.out.println("Updated order status for order ID " + event.getOrderId() + " to " + event.getDeliveryStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
