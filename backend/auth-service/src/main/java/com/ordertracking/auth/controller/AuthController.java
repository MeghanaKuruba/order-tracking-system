package com.ordertracking.auth.controller;

import com.ordertracking.auth.dto.*;
import com.ordertracking.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user. Validates the registration request, checks for duplicate email, encodes the password, and saves the user to the database. Throws exception if email already exists.
     * @param request
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        String response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Authenticate user and generate JWT token. Validates the login request, checks if user exists, verifies the password, and generates a JWT token. Throws exception if user not found or if password is invalid.
     * @param request
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get the current authenticated user's details. Returns the email and roles of the currently authenticated user.
     * @param authentication
     * @return
     */
    @GetMapping("/me")
    public ResponseEntity<?> currentUser(Authentication authentication) {
        return ResponseEntity.ok(Map.of("email", authentication.getName(),
                "roles", authentication.getAuthorities()));
    }

    /**
     * Refresh the JWT token using a valid refresh token. Validates the refresh token request, checks if the refresh token is valid and not expired, and generates a new JWT token. Throws exception if refresh token is invalid or expired.
     * @param request
     * @return
     */
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    /**
     * Logout the user by revoking the refresh token. Validates the refresh token request, checks if the refresh token is valid, and revokes it. Throws exception if refresh token is invalid.
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok("Logged out successfully");
    }
}
