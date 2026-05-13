package com.ordertracking.auth.service;

import com.ordertracking.auth.dto.AuthResponse;
import com.ordertracking.auth.dto.LoginRequest;
import com.ordertracking.auth.dto.RegisterRequest;

public interface AuthService {
    String register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
