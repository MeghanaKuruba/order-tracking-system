package com.ordertracking.payment.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordertracking.payment.dto.PaymentExpiredEvent;
import com.ordertracking.payment.dto.PaymentFailureEvent;
import com.ordertracking.payment.dto.PaymentSuccessEvent;
import com.ordertracking.payment.entity.OutboxEvent;
import com.ordertracking.payment.entity.OutboxStatus;
import com.ordertracking.payment.kafka.producer.PaymentEventProducer;
import com.ordertracking.payment.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final OutboxEventRepository repository;
    private final PaymentEventProducer producer;
    private final ObjectMapper mapper;

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {

        List<OutboxEvent> events =
                repository.findByStatus(OutboxStatus.PENDING);

        for (OutboxEvent event : events) {

            try {

                switch (event.getEventType()) {

                    case "PAYMENT_SUCCESS":

                        PaymentSuccessEvent success =
                                mapper.readValue(
                                        event.getPayload(),
                                        PaymentSuccessEvent.class);

                        producer.sendPaymentSuccessEvent(success);

                        break;

                    case "PAYMENT_FAILED":

                        PaymentFailureEvent failure =
                                mapper.readValue(
                                        event.getPayload(),
                                        PaymentFailureEvent.class);

                        producer.sendPaymentFailureEvent(failure);

                        break;

                    case "PAYMENT_EXPIRED":

                        PaymentExpiredEvent expiredEvent =
                                mapper.readValue(
                                        event.getPayload(),
                                        PaymentExpiredEvent.class);

                        producer.sendPaymentExpiredEvent(expiredEvent);

                        break;

                    default:
                        log.warn("Unknown event type: {}", event.getEventType());
                }

                event.setStatus(OutboxStatus.SENT);
                event.setProcessedAt(LocalDateTime.now());

                repository.save(event);

                log.info("Published Outbox Event {}", event.getId());

            } catch (Exception ex) {

                log.error("Failed to publish Outbox Event {}", event.getId(), ex);

                event.setStatus(OutboxStatus.FAILED);

                repository.save(event);
            }
        }
    }
}