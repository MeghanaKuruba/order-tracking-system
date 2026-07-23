package com.ordertracking.user.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordertracking.user.dto.UserCreatedEvent;
import com.ordertracking.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class UserCreatedConsumer {

    private final ObjectMapper objectMapper;

    private final UserProfileService userProfileService;

    @KafkaListener(
            topics = "user-created",
            groupId = "user-profile-group"
    )
    public void consume(String message) throws Exception {

        UserCreatedEvent event =
                objectMapper.readValue(
                        message,
                        UserCreatedEvent.class
                );

        userProfileService.createProfile(event);

        log.info(
                "User profile created for authUserId={}",
                event.getAuthUserId()
        );

    }

}