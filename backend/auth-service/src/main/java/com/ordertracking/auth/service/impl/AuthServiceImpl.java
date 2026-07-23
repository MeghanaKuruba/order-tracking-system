package com.ordertracking.auth.service.impl;

import com.ordertracking.auth.dto.*;
import com.ordertracking.auth.entity.RefreshToken;
import com.ordertracking.auth.entity.Role;
import com.ordertracking.auth.entity.User;
import com.ordertracking.auth.exception.DuplicateEmailException;
import com.ordertracking.auth.exception.InvalidPasswordException;
import com.ordertracking.auth.exception.UserNotFoundException;
import com.ordertracking.auth.kafka.producer.UserCreatedProducer;
import com.ordertracking.auth.repository.UserRepository;
import com.ordertracking.auth.security.JwtUtil;
import com.ordertracking.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final RefreshTokenServiceImpl refreshTokenService;

    private final JwtUtil jwtUtil;

    private final UserCreatedProducer userCreatedProducer;

    /**
     * Register a new customer. Validates the registration request, checks for duplicate email, encodes the password, and saves the user to the database. Throws exception if email already exists.
     * @param request
     * @return
     */
    @Override
    public String registerCustomer(RegisterRequest request) {

        return register(request, Role.CUSTOMER);
    }

    /**
     * Register a new restaurant owner. Validates the registration request, checks for duplicate email, encodes the password, and saves the user to the database. Throws exception if email already exists.
     * @param request
     * @return
     */
    @Override
    public String registerRestaurant(RegisterRequest request) {

        return register(request, Role.RESTAURANT_OWNER);
    }

    /**
     * Register a new delivery partner. Validates the registration request, checks for duplicate email, encodes the password, and saves the user to the database. Throws exception if email already exists.
     * @param request
     * @return
     */
    @Override
    public String registerDeliveryPartner(RegisterRequest request) {

        return register(request, Role.DELIVERY_PARTNER);
    }
    /**
     * Register a new user. Validates the registration request, checks for duplicate email, encodes the password, and saves the user to the database. Throws exception if email already exists.
     * @param request
     * @return
     */
    @Override
    public String register(RegisterRequest request, Role role) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email already exists: "+ request.getEmail());
        }

        User user = User.builder()
                .name(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(role)
                .build();

        userRepository.save(user);

        UserCreatedEvent event =
                UserCreatedEvent.builder()
                        .authUserId(user.getId())
                        .fullName(user.getName())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .role(user.getRole().name())
                        .build();

        userCreatedProducer.publish(event);

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

        String accessToken =
                jwtUtil.generateAccessToken(
                        user.getEmail(),
                        user.getRole()
                );

        RefreshToken refreshToken =
                refreshTokenService.issueRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .role(user.getRole())
                .build();
    }

    /**
     * Refresh JWT token using refresh token. Validates the refresh token, rotates it, and generates a new access token. Throws exception if refresh token is invalid or expired.
     * @param request
     * @return
     */
    @Override
    @Transactional
    public RefreshTokenResponse refreshToken(
            RefreshTokenRequest request
    ) {

        RefreshToken refreshToken =
                refreshTokenService.verifyRefreshToken(
                        request.getRefreshToken()
                );

        User user = refreshToken.getUser();

        String accessToken =
                jwtUtil.generateAccessToken(
                        user.getEmail(),
                        user.getRole()
                );

        RefreshToken updatedRefreshToken =
                refreshTokenService.issueRefreshToken(user);

        return RefreshTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(updatedRefreshToken.getToken())
                .build();
    }

    /**
     * Logout user by revoking the refresh token. Validates the refresh token and revokes it. Throws exception if refresh token is invalid or not found.
     * @param refreshToken
     */
    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
    }
}
