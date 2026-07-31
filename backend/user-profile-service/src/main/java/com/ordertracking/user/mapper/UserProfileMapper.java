package com.ordertracking.user.mapper;

import com.ordertracking.user.dto.AddressResponse;
import com.ordertracking.user.dto.UserProfileResponse;
import com.ordertracking.user.entity.Address;
import com.ordertracking.user.entity.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {

    public AddressResponse toAddressResponse(Address address) {

        return AddressResponse.builder()
                .id(address.getId())
                .label(address.getLabel())
                .recipientName(address.getRecipientName())
                .recipientPhone(address.getRecipientPhone())
                .doorNoOrBuildingName(address.getDoorNoOrBuildingName())
                .street(address.getStreet())
                .landmark(address.getLandmark())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .isDefault(address.getIsDefault())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }

    public UserProfileResponse toUserProfileResponse(UserProfile profile) {

        return UserProfileResponse.builder()
                .authUserId(profile.getAuthUserId())
                .fullName(profile.getFullName())
                .email(profile.getEmail())
                .phoneNumber(profile.getPhoneNumber())
                .gender(profile.getGender())
                .dateOfBirth(profile.getDateOfBirth())
                .profileImageUrl(profile.getProfileImageUrl())
                .updatedAt(profile.getUpdatedAt())
                .addresses(
                        profile.getAddresses()
                                .stream()
                                .map(this::toAddressResponse)
                                .toList()
                )
                .build();
    }

}