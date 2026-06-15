package com.ordertracking.payment.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordertracking.payment.dto.PaymentFailureEvent;
import com.ordertracking.payment.dto.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public void sendPaymentSuccessEvent(PaymentSuccessEvent event) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("payment-success", jsonMessage);
        } catch (Exception e) {
            throw new RuntimeException("Error publishing payment success event", e);
        }
    }

    public void sendPaymentFailureEvent(PaymentFailureEvent event){
        try {
            log.info("Publishing payment failure event for paymentId={}", event.getPaymentId());

            String jsonMessage = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("payment-failed", jsonMessage);

        } catch (Exception e) {
            throw new RuntimeException("Error publishing payment failure event", e);
        }
    }
}
