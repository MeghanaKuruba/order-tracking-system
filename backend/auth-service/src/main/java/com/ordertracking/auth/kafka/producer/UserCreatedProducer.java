package com.ordertracking.auth.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordertracking.auth.dto.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreatedProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public void publish(UserCreatedEvent event) {

        try {

            String payload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send("user-created", payload);

            log.info(
                    "Published UserCreatedEvent for authUserId={}",
                    event.getAuthUserId()
            );

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Unable to publish user-created event",
                    ex
            );

        }

    }

}