package com.ordertracking.user.service;

import com.ordertracking.user.dto.*;
import com.ordertracking.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserProfileService {

    void createProfile(UserCreatedEvent event);

    UserProfileResponse getProfile(Long authUserId);

    UserProfileResponse updateProfile(Long authUserId, UpdateProfileRequest request);

    UserProfileResponse addAddress(Long authUserId, AddAddressRequest request);

    List<AddressResponse> getAddresses(Long authUserId);

    AddressResponse updateAddress(Long authUserId, Long addressId, UpdateAddressRequest request);

    void deleteAddress(Long authUserId, Long addressId);

    AddressResponse setDefaultAddress(Long authUserId, Long addressId);

    AddressResponse getAddressById(Long authUserId, Long addressId);
}
