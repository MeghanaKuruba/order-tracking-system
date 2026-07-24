package com.ordertracking.user.controller;

import com.ordertracking.user.dto.UpdateProfileRequest;
import com.ordertracking.user.dto.UserProfileResponse;
import com.ordertracking.user.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService service;

    @GetMapping("/profile")
    public UserProfileResponse getProfile(@RequestHeader("X-Auth-UserId") Long authUserId) {
        return service.getProfile(authUserId);
    }

    @PutMapping("/update")
    public UserProfileResponse updateProfile(@RequestHeader("X-Auth-UserId") Long authUserId,
                                             @Valid @RequestBody UpdateProfileRequest request) {

        return service.updateProfile(authUserId, request);
    }

}
