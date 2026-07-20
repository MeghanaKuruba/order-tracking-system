package com.ordertracking.auth.controller;

import com.ordertracking.auth.dto.AuthResponse;
import com.ordertracking.auth.dto.LoginRequest;
import com.ordertracking.auth.dto.RegisterRequest;
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

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        String response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> currentUser(Authentication authentication) {

        return ResponseEntity.ok(Map.of(

                "email", authentication.getName(),

                "roles", authentication.getAuthorities()

        ));

    }
}
