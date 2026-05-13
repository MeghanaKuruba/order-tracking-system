package com.ordertracking.auth.service.impl;

import com.ordertracking.auth.dto.RegisterRequest;
import com.ordertracking.auth.entity.User;
import com.ordertracking.auth.exception.DuplicateEmailException;
import com.ordertracking.auth.repository.UserRepository;
import com.ordertracking.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
        @Override
        public String register(RegisterRequest request) {
            if(userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateEmailException("Email already exists: "+ request.getEmail());
            }

            User user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(request.getRole() != null ? request.getRole() : "USER")
                    .build();

            userRepository.save(user);

            return "User registered successfully";
        }
}
