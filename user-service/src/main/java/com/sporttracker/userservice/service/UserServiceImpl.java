package com.sporttracker.userservice.service;

import com.sporttracker.userservice.cache.UserCacheService;
import com.sporttracker.userservice.dto.LoginRequest;
import com.sporttracker.userservice.dto.LoginResponse;
import com.sporttracker.userservice.dto.RegisterRequest;
import com.sporttracker.userservice.model.User;
import com.sporttracker.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserCacheService userCacheService;

    @Override
    public User register(RegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        User saved = userRepository.save(user);
        userCacheService.evict(saved.getEmail());
        return saved;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userCacheService.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}