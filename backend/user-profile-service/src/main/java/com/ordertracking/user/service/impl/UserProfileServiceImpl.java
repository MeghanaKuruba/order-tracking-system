package com.ordertracking.user.service.impl;

import com.ordertracking.user.dto.UserCreatedEvent;
import com.ordertracking.user.dto.UserProfileResponse;
import com.ordertracking.user.dto.UpdateProfileRequest;
import com.ordertracking.user.dto.AddressResponse;
import com.ordertracking.user.entity.Role;
import com.ordertracking.user.entity.UserProfile;
import com.ordertracking.user.exception.InvalidProfileException;
import com.ordertracking.user.exception.UserProfileNotFoundException;
import com.ordertracking.user.repository.UserProfileRepository;
import com.ordertracking.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

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
                .role(Role.valueOf(event.getRole()))
                .createdAt(LocalDateTime.now())
                .build();

        // Save the new user profile to the database
        userProfileRepository.save(userProfile);

        log.info("User profile created for authUserId={}", event.getAuthUserId());
    }

    @Override
    public UserProfileResponse getProfile(Long authUserId) {

        UserProfile profile =
                userProfileRepository.findByAuthUserId(authUserId)
                        .orElseThrow(() ->
                                new UserProfileNotFoundException("Profile not found"));

        return map(profile);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long authUserId, UpdateProfileRequest request) {

        UserProfile profile =
                userProfileRepository.findByAuthUserId(authUserId)
                        .orElseThrow(() ->
                                new UserProfileNotFoundException("Profile not found"));

        if (request.getFullName() != null &&
                request.getFullName().length() < 3) {

            throw new InvalidProfileException(
                    "Full name must contain at least 3 characters."
            );
        }else {
            profile.setFullName(request.getFullName());
        }

        if (request.getPhoneNumber() != null)
            profile.setPhoneNumber(request.getPhoneNumber());

        if (request.getGender() != null)
            profile.setGender(request.getGender());

        if (request.getDateOfBirth() != null &&
                request.getDateOfBirth().isAfter(LocalDate.now())) {

            throw new InvalidProfileException(
                    "Date of birth cannot be in the future."
            );
        }else {
            profile.setDateOfBirth(request.getDateOfBirth());
        }

        if (request.getProfileImageUrl() != null)
            profile.setProfileImageUrl(request.getProfileImageUrl());

        return map(userProfileRepository.save(profile));
    }

    private UserProfileResponse map(UserProfile profile) {

        return UserProfileResponse.builder()
                .authUserId(profile.getAuthUserId())
                .fullName(profile.getFullName())
                .email(profile.getEmail())
                .phoneNumber(profile.getPhoneNumber())
                .gender(profile.getGender())
                .dateOfBirth(profile.getDateOfBirth())
                .profileImageUrl(profile.getProfileImageUrl())
                .addresses(
                        profile.getAddresses()
                                .stream()
                                .map(address ->
                                        AddressResponse.builder()
                                                .id(address.getId())
                                                .label(address.getLabel())
                                                .recipientName(address.getRecipientName())
                                                .recipientPhone(address.getRecipientPhone())
                                                .doorNo(address.getDoorNo())
                                                .street(address.getStreet())
                                                .landmark(address.getLandmark())
                                                .city(address.getCity())
                                                .state(address.getState())
                                                .country(address.getCountry())
                                                .postalCode(address.getPostalCode())
                                                .latitude(address.getLatitude())
                                                .longitude(address.getLongitude())
                                                .isDefault(address.getIsDefault())
                                                .userProfile(profile)
                                                .createdAt(address.getCreatedAt())
                                                .updatedAt(address.getUpdatedAt())
                                                .build()
                                )
                                .toList()
                )
                .build();
    }
}
