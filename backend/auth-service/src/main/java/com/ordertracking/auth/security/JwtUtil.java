package com.ordertracking.auth.security;

import com.ordertracking.auth.entity.Role;
import com.ordertracking.auth.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String secret = "mysecretkeymysecretkeymysecretkeymysecretkey"; // 256-bit key for HS256
    private final long expiration = 3600000; // 1 hour in milliseconds

    public String generateToken(String email, Role role) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role.name())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expiration))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }
}
