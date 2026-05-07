package com.sporttracker.userservice.controller;

import com.sporttracker.shared.response.ApiResponse;
import com.sporttracker.userservice.dto.LoginRequest;
import com.sporttracker.userservice.dto.LoginResponse;
import com.sporttracker.userservice.dto.RegisterRequest;
import com.sporttracker.userservice.model.User;
import com.sporttracker.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ApiResponse<User> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(userService.register(request), "Kullanıcı başarıyla kaydedildi");
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(userService.login(request), "Giriş başarılı");
    }
}