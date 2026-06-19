package com.ordertracking.payment.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordertracking.payment.service.PaymentWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentWebhookServiceImpl implements PaymentWebhookService {

    private final ObjectMapper objectMapper;

    @Override
    public void processWebhook(String payload) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            String eventType = rootNode.get("event").asText();

            log.info("Received webhook event: {}", eventType);

            switch (eventType){
                case "Payment.captured":
                    log.info("Received payment success event");
                    break;

                case "payment.failed":
                    log.info("Received payment failure event");
                    break;

                default:
                    log.info("Ignoring unsupported event: {}", eventType);
            }
        }catch (Exception ex){
            log.error("Error processing webhook payload", ex);
        }
    }
}
