package com.ordertracking.user.service.impl;

import com.ordertracking.user.dto.UserCreatedEvent;
import com.ordertracking.user.entity.UserProfile;
import com.ordertracking.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl {

    private final UserProfileRepository userProfileRepository;

    public void createProfile(UserCreatedEvent event) {
        // Check if the user profile already exists
        if (userProfileRepository.existsByAuthUserId(event.getAuthUserId())) {
            log.warn("User profile already exists for authUserId={}", event.getAuthUserId());
            return;
        }

        // Create a new UserProfile entity
        UserProfile userProfile = UserProfile.builder()
                .authUserId(event.getAuthUserId())
                .fullName(event.getFullName())
                .email(event.getEmail())
                .phoneNumber(event.getPhoneNumber())
                .role(event.getRole())
                .createdAt(LocalDateTime.now())
                .build();

        // Save the new user profile to the database
        userProfileRepository.save(userProfile);

        log.info("User profile created for authUserId={}", event.getAuthUserId());
    }
}
