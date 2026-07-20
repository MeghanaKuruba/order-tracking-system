package com.ordertracking.auth.dto;

import com.ordertracking.auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Role role;
}
