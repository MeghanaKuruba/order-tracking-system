package com.ordertracking.payment.kafka;

import com.ordertracking.payment.dto.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

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
}
