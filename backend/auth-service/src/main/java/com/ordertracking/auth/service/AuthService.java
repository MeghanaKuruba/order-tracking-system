package com.ordertracking.auth.service;

import com.ordertracking.auth.dto.*;

public interface AuthService {
    String register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    void logout(String refreshToken);
}
