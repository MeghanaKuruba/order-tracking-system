package com.ordertracking.user.service;

import com.ordertracking.user.dto.UserCreatedEvent;
import com.ordertracking.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileService extends JpaRepository<UserProfile, Long> {

    void createProfile(UserCreatedEvent event);
}
