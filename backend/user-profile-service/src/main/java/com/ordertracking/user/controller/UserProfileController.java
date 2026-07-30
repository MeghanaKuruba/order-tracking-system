package com.ordertracking.user.controller;

import com.ordertracking.user.dto.*;
import com.ordertracking.user.service.location.LocationValidationService;
import com.ordertracking.user.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService service;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@RequestHeader("X-Auth-UserId") Long authUserId) {
        return ResponseEntity.ok(service.getProfile(authUserId));
    }

    @PutMapping("/update")
    public ResponseEntity<UserProfileResponse> updateProfile(@RequestHeader("X-Auth-UserId") Long authUserId,
                                             @Valid @RequestBody UpdateProfileRequest request) {

        return ResponseEntity.ok(service.updateProfile(authUserId, request));
    }

    @PostMapping("/profile/add/addresses")
    public ResponseEntity<UserProfileResponse> addAddress( @RequestHeader("X-Auth-UserId") Long authUserId,
                                           @Valid @RequestBody AddAddressRequest request){
        return ResponseEntity.ok(service.addAddress(authUserId, request));
    }

    @GetMapping("/profile/get/addresses")
    public ResponseEntity<List<AddressResponse>> getAddresses(@RequestHeader("X-Auth-UserId") Long authUserId){

        return ResponseEntity.ok(service.getAddresses(authUserId));
    }

    @PutMapping("/profile/addresses/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @RequestHeader("X-Auth-UserId") Long authUserId,
            @PathVariable Long addressId,
            @Valid @RequestBody UpdateAddressRequest request
    ){

        return ResponseEntity.ok(service.updateAddress(authUserId, addressId, request));
    }

    @DeleteMapping("/profile/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@RequestHeader("X-Auth-UserId") Long authUserId,
                                                @PathVariable Long addressId){
        service.deleteAddress(authUserId, addressId);
        return ResponseEntity.ok("Address deleted successfully.");
    }

    @PatchMapping("/profile/addresses/{addressId}/default")
    public ResponseEntity<AddressResponse> setDefaultAddress(@RequestHeader("X-Auth-UserId") Long authUserId,
                                             @PathVariable Long addressId){

        return ResponseEntity.ok(service.setDefaultAddress(authUserId, addressId));
    }
}
