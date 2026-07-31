package com.ordertracking.user.service.impl;

import com.ordertracking.user.dto.*;
import com.ordertracking.user.entity.Address;
import com.ordertracking.user.entity.Role;
import com.ordertracking.user.entity.UserProfile;
import com.ordertracking.user.exception.*;
import com.ordertracking.user.mapper.UserProfileMapper;
import com.ordertracking.user.repository.AddressRepository;
import com.ordertracking.user.repository.UserProfileRepository;
import com.ordertracking.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    private final AddressRepository addressRepository;

    private final UserProfileMapper userProfileMapper;

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

        return userProfileMapper.toUserProfileResponse(profile);
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

        return userProfileMapper.toUserProfileResponse(userProfileRepository.save(profile));
    }

    @Override
    @Transactional
    public UserProfileResponse addAddress(Long authUserId, AddAddressRequest request) {

        UserProfile profile =
                userProfileRepository.findByAuthUserId(authUserId)
                        .orElseThrow(() ->
                                new UserProfileNotFoundException(
                                        "Profile not found"
                                ));

        Optional<Address> existingAddress =
                addressRepository
                        .findByUserProfileAuthUserIdAndDoorNoOrBuildingNameIgnoreCaseAndStreetIgnoreCaseAndCityIgnoreCaseAndStateIgnoreCaseAndCountryIgnoreCaseAndPostalCode(
                                authUserId,
                                request.getDoorNoOrBuildingName(),
                                request.getStreet(),
                                request.getCity(),
                                request.getState(),
                                request.getCountry(),
                                request.getPostalCode()
                        );

        if (existingAddress.isPresent()) {
            throw new DuplicateAddressException(
                    "Address already exists."
            );
        }

        Address address =
                Address.builder()
                        .label(request.getLabel())
                        .recipientName(request.getRecipientName())
                        .recipientPhone(request.getRecipientPhone())
                        .doorNoOrBuildingName(request.getDoorNoOrBuildingName())
                        .street(request.getStreet())
                        .landmark(request.getLandmark())
                        .city(request.getCity())
                        .state(request.getState())
                        .country(request.getCountry())
                        .postalCode(request.getPostalCode())
                        .latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .userProfile(profile)
                        .build();

        if(profile.getAddresses().isEmpty()){
            address.setIsDefault(true);
        }

        profile.getAddresses().add(address);

        userProfileRepository.save(profile);

        return userProfileMapper.toUserProfileResponse(profile);
    }

    @Override
    public List<AddressResponse> getAddresses(Long authUserId) {

        return addressRepository
                .findByUserProfileAuthUserIdOrderByCreatedAtAsc(authUserId)
                .stream()
                .map(userProfileMapper::toAddressResponse)
                .toList();

    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long authUserId, Long addressId, UpdateAddressRequest request) {

        Address address =
                addressRepository
                        .findByIdAndUserProfileAuthUserId(addressId, authUserId)
                        .orElseThrow(() -> new AddressNotFoundException("Address not found."));

        Optional<Address> duplicate =
                addressRepository
                        .findByUserProfileAuthUserIdAndDoorNoOrBuildingNameIgnoreCaseAndStreetIgnoreCaseAndCityIgnoreCaseAndStateIgnoreCaseAndCountryIgnoreCaseAndPostalCode(
                                authUserId,
                                request.getDoorNoOrBuildingName(),
                                request.getStreet(),
                                request.getCity(),
                                request.getState(),
                                request.getCountry(),
                                request.getPostalCode()
                        );

        if (duplicate.isPresent()
                && !duplicate.get().getId().equals(addressId)) {
            throw new DuplicateAddressException("Address already exists.");
        }

        address.setLabel(request.getLabel());
        address.setRecipientName(request.getRecipientName());
        address.setRecipientPhone(request.getRecipientPhone());
        address.setDoorNoOrBuildingName(request.getDoorNoOrBuildingName());
        address.setStreet(request.getStreet());
        address.setLandmark(request.getLandmark());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());

        addressRepository.save(address);

        return userProfileMapper.toAddressResponse(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long authUserId, Long addressId) {
        Address address =
                addressRepository
                        .findByIdAndUserProfileAuthUserId(addressId, authUserId)
                        .orElseThrow(() -> new AddressNotFoundException("Address not found."));

        boolean wasDefault = Boolean.TRUE.equals(address.getIsDefault());

        addressRepository.delete(address);

        if(wasDefault){
            List<Address> remaining =
                    addressRepository.findByUserProfileAuthUserIdOrderByCreatedAtAsc(authUserId);

            Address first = remaining.get(0);
            first.setIsDefault(true);

            addressRepository.save(first);
        }

    }

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(Long authUserId, Long addressId) {
        Address address =
                addressRepository
                        .findByIdAndUserProfileAuthUserId(addressId, authUserId)
                        .orElseThrow(() -> new AddressNotFoundException("Address not found."));

        List<Address> addresses =
                addressRepository.findByUserProfileAuthUserIdOrderByCreatedAtAsc(authUserId);

        for (Address addr : addresses) {
            if (Boolean.TRUE.equals(addr.getIsDefault())) {
                addr.setIsDefault(false);
            }
        }

        address.setIsDefault(true);

        addressRepository.saveAll(addresses);

        Address updated = addressRepository.save(address);

        return userProfileMapper.toAddressResponse(updated);
    }

    @Override
    public AddressResponse getAddressById(Long authUserId, Long addressId) {

        Address address =
                addressRepository
                        .findByIdAndUserProfileAuthUserId(addressId, authUserId)
                        .orElseThrow(() ->
                                new AddressNotFoundException("Address not found."));

        return userProfileMapper.toAddressResponse(address);

    }

}
