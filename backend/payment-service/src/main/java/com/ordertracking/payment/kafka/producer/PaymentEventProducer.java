package com.ordertracking.payment.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordertracking.payment.dto.PaymentEvent;
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

    public void sendPaymentEvent(PaymentEvent event) {

        try {
            log.info("Publishing payment event status={} for paymentId={}", event.getPaymentStatus(), event.getPaymentId());

            String jsonMessage = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("payment-event", jsonMessage);

        } catch (Exception e) {
            throw new RuntimeException("Error publishing payment event", e);
        }
    }
}
