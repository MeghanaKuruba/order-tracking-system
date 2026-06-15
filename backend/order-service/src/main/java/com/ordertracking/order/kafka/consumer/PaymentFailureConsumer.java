package com.ordertracking.order.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordertracking.order.dto.PaymentFailureEvent;
import com.ordertracking.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentFailureConsumer {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @KafkaListener(topics = "payment-failed", groupId = "order-group")
    public void consumePaymentFailure(String message) {
        try {
            PaymentFailureEvent event =
                    objectMapper.readValue(message, PaymentFailureEvent.class);

            log.info("Received payment failure event for paymentId={}", event.getPaymentId());

            // Update order status
            orderService.updateOrderStatus(event.getOrderId(), "FAILED");

        } catch (Exception e) {
            log.error("Error processing payment failure event: {}", e.getMessage(), e);
        }
    }
}
