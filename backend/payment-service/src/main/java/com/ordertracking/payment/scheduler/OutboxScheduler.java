package com.ordertracking.payment.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordertracking.payment.config.OutboxRetryProperties;
import com.ordertracking.payment.entity.OutboxEvent;
import com.ordertracking.payment.entity.OutboxStatus;
import com.ordertracking.payment.dto.PaymentEvent;
import com.ordertracking.payment.kafka.producer.PaymentEventProducer;
import com.ordertracking.payment.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final OutboxEventRepository repository;
    private final PaymentEventProducer producer;
    private final ObjectMapper mapper;
    private final OutboxRetryProperties retryProperties;

    @Transactional
    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {

        LocalDateTime retryTime =
                LocalDateTime.now()
                        .minusSeconds(
                                retryProperties.getRetryIntervalSeconds()
                        );

        List<OutboxEvent> events =
                repository.findByStatusAndLastRetryAtBeforeOrStatusAndLastRetryAtIsNull(
                        OutboxStatus.PENDING,
                        retryTime,
                        OutboxStatus.PENDING
                );

        for (OutboxEvent event : events) {

            try {

                PaymentEvent paymentEvent =
                        mapper.readValue(
                                event.getPayload(),
                                PaymentEvent.class);

                producer.sendPaymentEvent(paymentEvent);

                event.setStatus(OutboxStatus.SENT);
                event.setProcessedAt(LocalDateTime.now());

                repository.save(event);

                log.info("Published Outbox Event {}", event.getId());

            } catch (Exception ex) {

                log.error("Failed to publish Outbox Event {}", event.getId(), ex);

                event.setRetryCount(event.getRetryCount() + 1);

                event.setLastRetryAt(
                        LocalDateTime.now()
                );

                if(event.getRetryCount()
                        >= retryProperties.getMaxAttempts()) {

                    event.setStatus(
                            OutboxStatus.FAILED
                    );

                    log.error(
                            "Outbox event {} permanently failed after {} retries",
                            event.getId(),
                            event.getRetryCount()
                    );

                }
                else{

                    event.setStatus(
                            OutboxStatus.PENDING
                    );

                    log.warn(
                            "Retry {} scheduled for Outbox event {}",
                            event.getRetryCount(),
                            event.getId()
                    );

                }

                repository.save(event);
            }
        }
    }
}