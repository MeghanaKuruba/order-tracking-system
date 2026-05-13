package com.ordertracking.auth.service;

import com.ordertracking.auth.dto.RegisterRequest;

public interface AuthService {
    String register(RegisterRequest request);
}
