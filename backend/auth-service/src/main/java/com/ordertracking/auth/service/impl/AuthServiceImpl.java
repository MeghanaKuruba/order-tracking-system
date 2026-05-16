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

    /**
     * Register a new user. Validates the registration request, checks for duplicate email, encodes the password, and saves the user to the database. Throws exception if email already exists.
     * @param request
     * @return
     */
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

    /**
     * Authenticate user and generate JWT token. Validates the login request, checks if user exists, verifies the password, and generates a JWT token. Throws exception if user not found or if password is invalid.
     * @param request
     * @return
     */
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
