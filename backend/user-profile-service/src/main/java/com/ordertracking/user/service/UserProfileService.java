package com.ordertracking.user.service;

import com.ordertracking.user.dto.UpdateProfileRequest;
import com.ordertracking.user.dto.UserCreatedEvent;
import com.ordertracking.user.dto.UserProfileResponse;
import com.ordertracking.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileService {

    void createProfile(UserCreatedEvent event);

    UserProfileResponse getProfile(Long authUserId);

    UserProfileResponse updateProfile(
            Long authUserId,
            UpdateProfileRequest request
    );
}
