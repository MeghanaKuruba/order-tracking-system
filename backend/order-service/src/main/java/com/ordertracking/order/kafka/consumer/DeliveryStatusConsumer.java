package com.ordertracking.order.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordertracking.order.dto.DeliveryStatusUpdatedEvent;
import com.ordertracking.order.entity.Order;
import com.ordertracking.order.entity.OrderStatus;
import com.ordertracking.order.exception.OrderNotFoundException;
import com.ordertracking.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryStatusConsumer {

    private final ObjectMapper objectMapper;

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "delivery-status-updated", groupId = "order-service")
    public void consumeDeliveryStatusUpdatedEvent(String message) {
        try {
            DeliveryStatusUpdatedEvent event = objectMapper.readValue(message, DeliveryStatusUpdatedEvent.class);
            Order order = orderRepository.findById(event.getOrderId())
                    .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + event.getOrderId()));
            order.setStatus(OrderStatus.valueOf(event.getDeliveryStatus())); // valueOf converts the string to the corresponding enum constant
            orderRepository.save(order);
            System.out.println("Updated order status for order ID " + event.getOrderId() + " to " + event.getDeliveryStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
