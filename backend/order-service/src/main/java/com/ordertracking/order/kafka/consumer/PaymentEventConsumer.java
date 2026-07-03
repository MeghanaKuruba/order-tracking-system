package com.ordertracking.order.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordertracking.order.dto.OrderConfirmedEvent;
import com.ordertracking.order.dto.PaymentExpiredEvent;
import com.ordertracking.order.dto.PaymentFailureEvent;
import com.ordertracking.order.dto.PaymentSuccessEvent;
import com.ordertracking.order.entity.Order;
import com.ordertracking.order.entity.OrderStatus;
import com.ordertracking.order.exception.OrderNotFoundException;
import com.ordertracking.order.kafka.producer.OrderConfirmedEventProducer;
import com.ordertracking.order.repository.OrderRepository;
import com.ordertracking.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;
    private final OrderConfirmedEventProducer orderConfirmedEventProducer;
    private final OrderService orderService;

    @KafkaListener(topics = "payment-success", groupId = "order-group")
    public void consumePaymentSuccess(String message) {

        try {

            PaymentSuccessEvent event =
                    objectMapper.readValue(message, PaymentSuccessEvent.class);

            log.info("Received payment success event for paymentId={}", event.getPaymentId());

            Order order = orderRepository.findById(event.getOrderId())
                    .orElseThrow(() ->
                            new OrderNotFoundException(
                                    "Order not found with ID: " + event.getOrderId()));

            order.setStatus(OrderStatus.CONFIRMED);

            Order savedOrder = orderRepository.save(order);

            OrderConfirmedEvent confirmedEvent =
                    new OrderConfirmedEvent(
                            savedOrder.getOrderId(),
                            savedOrder.getRestaurantId(),
                            savedOrder.getCustomerId(),
                            savedOrder.getStatus().name(),
                            savedOrder.getTotalAmount()
                    );

            orderConfirmedEventProducer.sendOrderConfirmedEvent(confirmedEvent);

            log.info("Order {} confirmed successfully", savedOrder.getOrderId());

        } catch (Exception e) {

            log.error("Error processing payment success event", e);
        }
    }

    @KafkaListener(topics = "payment-failed", groupId = "order-group")
    public void consumePaymentFailure(String message) {

        try {

            PaymentFailureEvent event =
                    objectMapper.readValue(message, PaymentFailureEvent.class);

            log.info("Received payment failure event for paymentId={}", event.getPaymentId());

            orderService.updateOrderStatus(event.getOrderId(), "FAILED");

        } catch (Exception e) {

            log.error("Error processing payment failure event", e);
        }
    }

    @KafkaListener(topics = "payment-expired", groupId = "order-group")
    public void consumePaymentExpired(String message) {

        try {

            PaymentExpiredEvent event =
                    objectMapper.readValue(message, PaymentExpiredEvent.class);

            log.info("Received payment expired event for paymentId={}", event.getPaymentId());

            orderService.updateOrderStatus(event.getOrderId(), "EXPIRED");

        } catch (Exception e) {

            log.error("Error processing payment expired event", e);
        }
    }
}