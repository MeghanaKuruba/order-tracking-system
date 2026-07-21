package com.ordertracking.auth.security;

import com.ordertracking.auth.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String secret = "mysecretkeymysecretkeymysecretkeymysecretkey";

    private final long accessTokenExpiration = 30 * 1000; // 30 sec // 15 * 60 * 1000; // 15 mins
    private final long refreshTokenExpiration = 7L * 24 * 60 * 60 * 1000; // 7 days

    public String generateAccessToken(String email, Role role) {

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role.name())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + accessTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    public String extractEmail(String token) {

        return getClaims(token).getSubject();
    }

    public String extractRole(String token) {

        return getClaims(token)
                .get("role", String.class);
    }

    public boolean validateToken(String token) {

        try {
            getClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateRefreshToken(
            String email
    ) {

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

}
