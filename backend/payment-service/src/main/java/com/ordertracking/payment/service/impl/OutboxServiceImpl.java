package com.ordertracking.payment.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordertracking.payment.entity.OutboxEvent;
import com.ordertracking.payment.entity.OutboxStatus;
import com.ordertracking.payment.repository.OutboxEventRepository;
import com.ordertracking.payment.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxServiceImpl implements OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void saveEvent(String aggregateType, Long aggregateId,
                          String eventType, Object payload) {

        try {

            OutboxEvent event = OutboxEvent.builder()
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .payload(objectMapper.writeValueAsString(payload))
                    .retryCount(0)
                    .status(OutboxStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .lastRetryAt(null)
                    .build();

            outboxEventRepository.save(event);

            log.info("Outbox event created : {}", eventType);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}