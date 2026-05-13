package com.ordertracking.auth.service.impl;

import com.ordertracking.auth.dto.AuthResponse;
import com.ordertracking.auth.dto.LoginRequest;
import com.ordertracking.auth.dto.RegisterRequest;
import com.ordertracking.auth.entity.User;
import com.ordertracking.auth.exception.DuplicateEmailException;
import com.ordertracking.auth.exception.InvalidPasswordException;
import com.ordertracking.auth.exception.UserNotFoundException;
import com.ordertracking.auth.repository.UserRepository;
import com.ordertracking.auth.security.JwtUtil;
import com.ordertracking.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

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

    @Override
    public AuthResponse login(LoginRequest request) {

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));

            if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new InvalidPasswordException("Invalid password");
            }

            String token= jwtUtil.generateToken(user.getEmail(), user.getRole());

        return new AuthResponse(token, user.getRole());
    }


}
