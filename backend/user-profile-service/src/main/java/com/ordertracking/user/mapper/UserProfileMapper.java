package com.ordertracking.user.mapper;

import com.ordertracking.user.dto.*;
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
                .createdAt(profile.getCreatedAt())
                .addresses(
                        profile.getAddresses()
                                .stream()
                                .map(this::toAddressResponse)
                                .toList()
                )
                .build();
    }

    public AddressValidationRequest toAddressValidationRequest(AddAddressRequest request) {

        return AddressValidationRequest.builder()
                .doorNoOrBuildingName(request.getDoorNoOrBuildingName())
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .build();

    }

    public AddressValidationRequest toAddressValidationRequest(UpdateAddressRequest request) {

        return AddressValidationRequest.builder()
                .doorNoOrBuildingName(request.getDoorNoOrBuildingName())
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .build();

    }

    public Address toAddress(
            AddAddressRequest request,
            UserProfile profile,
            AddressValidationResponse validation
    ) {

        return Address.builder()
                .label(request.getLabel())
                .recipientName(request.getRecipientName())
                .recipientPhone(request.getRecipientPhone())
                .doorNoOrBuildingName(request.getDoorNoOrBuildingName())
                .street(request.getStreet())
                .landmark(request.getLandmark())
                .city(validation.getCity())
                .state(validation.getState())
                .country(validation.getCountry())
                .postalCode(validation.getPostalCode())
                .latitude(validation.getLatitude())
                .longitude(validation.getLongitude())
                .userProfile(profile)
                .build();
    }

    public void updateAddress(
            Address address,
            UpdateAddressRequest request,
            AddressValidationResponse validation
    ) {

        address.setLabel(request.getLabel());
        address.setRecipientName(request.getRecipientName());
        address.setRecipientPhone(request.getRecipientPhone());
        address.setDoorNoOrBuildingName(request.getDoorNoOrBuildingName());
        address.setStreet(request.getStreet());
        address.setLandmark(request.getLandmark());

        address.setCity(validation.getCity());
        address.setState(validation.getState());
        address.setCountry(validation.getCountry());
        address.setPostalCode(validation.getPostalCode());

        address.setLatitude(validation.getLatitude());
        address.setLongitude(validation.getLongitude());

    }

}