package com.ordertracking.auth.service;

import com.ordertracking.auth.dto.*;
import com.ordertracking.auth.entity.Role;

public interface AuthService {
    String registerCustomer(RegisterRequest request);

    String registerRestaurant(RegisterRequest request);

    String registerDeliveryPartner(RegisterRequest request);

    String register(RegisterRequest request, Role role);

    AuthResponse login(LoginRequest request);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    void logout(String refreshToken);
}
