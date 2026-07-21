package com.ordertracking.auth.service.impl;

import com.ordertracking.auth.entity.RefreshToken;
import com.ordertracking.auth.entity.User;
import com.ordertracking.auth.exception.RefreshTokenException;
import com.ordertracking.auth.repository.RefreshTokenRepository;
import com.ordertracking.auth.security.JwtUtil;
import com.ordertracking.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repository;

    private final JwtUtil jwtUtil;

    /**
     * Issues a new refresh token for the given user. If a refresh token already exists for the user, it updates the existing token. The new token is generated using the user's email and has an expiry date of 7 days from the current time.
     * @param user The user for whom the refresh token is to be issued.
     * @return The issued or updated RefreshToken entity.
     */
    @Override
    @Transactional
    public RefreshToken issueRefreshToken(User user) {

        RefreshToken refreshToken =
                repository.findByUser(user)
                        .orElse(new RefreshToken());

        refreshToken.setUser(user);
        refreshToken.setToken(
                jwtUtil.generateRefreshToken(user.getEmail())
        );
        refreshToken.setExpiryDate(
                LocalDateTime.now().plusDays(7)
        );
        refreshToken.setRevoked(false);

        return repository.save(refreshToken);
    }

    /**
     * Verifies the provided refresh token. It checks if the token exists, is not revoked, and has not expired. If any of these conditions fail, a RefreshTokenException is thrown.
     * @param token The refresh token to be verified.
     * @return The valid RefreshToken entity.
     * @throws RefreshTokenException if the token is invalid, revoked, or expired.
     */
    @Override
    public RefreshToken verifyRefreshToken(String token) {

        RefreshToken refreshToken =
                repository.findByToken(token)
                        .orElseThrow(() -> new RefreshTokenException("Invalid Refresh Token"));

        if (refreshToken.isRevoked()) {
            throw new RefreshTokenException("Refresh Token Revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RefreshTokenException("Refresh Token Expired");
        }
        return refreshToken;
    }

    /**
     * Revokes the provided refresh token. It marks the token as revoked in the database, preventing its future use.
     * @param token The refresh token to be revoked.
     * @throws RuntimeException if the token is not found in the database.
     */
    @Override
    @Transactional
    public void revokeRefreshToken(String token) {

        RefreshToken refreshToken =
                repository.findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException("Refresh token not found"));

        refreshToken.setRevoked(true);

        repository.save(refreshToken);

    }
}