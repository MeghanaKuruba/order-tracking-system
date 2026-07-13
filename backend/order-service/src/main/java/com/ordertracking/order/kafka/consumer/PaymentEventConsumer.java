package com.ordertracking.order.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordertracking.order.dto.*;
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

    @KafkaListener(topics = "payment-event", groupId = "order-group")
    public void consumePaymentEvent(String message) {

        try {

            PaymentEvent event =
                    objectMapper.readValue(message, PaymentEvent.class);

            log.info(
                    "Received payment event {} for paymentId={}",
                    event.getPaymentStatus(),
                    event.getPaymentId()
            );

            switch (event.getPaymentStatus()) {

                case "SUCCESS":

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

                    orderConfirmedEventProducer.sendOrderConfirmedEvent(
                            confirmedEvent
                    );

                    log.info(
                            "Order {} confirmed successfully",
                            savedOrder.getOrderId()
                    );

                    break;

                case "FAILED":

                    orderService.updateOrderStatus(
                            event.getOrderId(),
                            "FAILED"
                    );

                    break;

                case "EXPIRED":

                    orderService.updateOrderStatus(
                            event.getOrderId(),
                            "EXPIRED"
                    );

                    break;

                default:

                    log.warn(
                            "Unhandled payment status {}",
                            event.getPaymentStatus()
                    );
            }

        } catch (Exception e) {

            log.error("Error processing payment event", e);

        }
    }
}