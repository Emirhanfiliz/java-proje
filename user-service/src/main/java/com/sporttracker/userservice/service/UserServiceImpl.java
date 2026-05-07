package com.sporttracker.userservice.service;

import com.sporttracker.shared.exception.CustomBusinessException;
import com.sporttracker.userservice.cache.UserCacheService;
import com.sporttracker.userservice.dto.LoginRequest;
import com.sporttracker.userservice.dto.LoginResponse;
import com.sporttracker.userservice.dto.RegisterRequest;
import com.sporttracker.userservice.model.User;
import com.sporttracker.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomBusinessException(
                    "Bu email adresi zaten kayıtlı: " + request.getEmail(),
                    HttpStatus.CONFLICT
            );
        }

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
                .orElseThrow(() -> new CustomBusinessException(
                        "Kullanıcı bulunamadı: " + request.getEmail(),
                        HttpStatus.NOT_FOUND
                ));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomBusinessException("Şifre hatalı", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}