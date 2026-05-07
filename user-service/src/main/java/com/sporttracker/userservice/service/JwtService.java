package com.sporttracker.userservice.service;

import com.sporttracker.shared.security.JwtTokenProvider;
import com.sporttracker.shared.security.TokenValidationResult;
import com.sporttracker.userservice.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtTokenProvider jwtTokenProvider;

    public String generateToken(User user) {
        return jwtTokenProvider.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }

    public String generateRefreshToken(User user) {
        return jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail());
    }

    public String extractEmail(String token) {
        return jwtTokenProvider.extractEmail(token);
    }

    public boolean isTokenValid(String token) {
        TokenValidationResult result = jwtTokenProvider.validateToken(token);
        return result.isValid();
    }
}
