package com.ordertracking.order.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordertracking.order.dto.OrderConfirmedEvent;
import com.ordertracking.order.dto.PaymentSuccessEvent;
import com.ordertracking.order.entity.Order;
import com.ordertracking.order.entity.OrderStatus;
import com.ordertracking.order.exception.OrderNotFoundException;
import com.ordertracking.order.kafka.producer.OrderConfirmedEventProducer;
import com.ordertracking.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentSuccessConsumer {

    private final ObjectMapper objectMapper;

    private final OrderRepository orderRepository;

    private final OrderConfirmedEventProducer orderConfirmedEventProducer;

    @KafkaListener(topics = "payment-success", groupId = "order-group")
    public void consume(String message) {
        try {
            PaymentSuccessEvent event = objectMapper.readValue(message, PaymentSuccessEvent.class);
            Order order = orderRepository.findById(event.getOrderId()).orElseThrow(()-> new OrderNotFoundException("Order not found with ID: " + event.getOrderId()));

            order.setStatus(OrderStatus.CONFIRMED);
            Order savedOrder = orderRepository.save(order);
            OrderConfirmedEvent confirmedEvent = new OrderConfirmedEvent(
                    savedOrder.getOrderId(),
                    savedOrder.getRestaurantId(),
                    savedOrder.getCustomerId(),
                    savedOrder.getStatus().name(),
                    savedOrder.getTotalAmount()
            );
            orderConfirmedEventProducer.sendOrderConfirmedEvent(confirmedEvent);
            System.out.println("Order confirmed for Order ID " + event.getOrderId());

        } catch (Exception e) {
            System.out.println("Failed to process payment success event: " + e.getMessage());
        }
    }
}
