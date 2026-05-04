package com.sporttracker.userservice.service;

import com.sporttracker.userservice.dto.LoginRequest;
import com.sporttracker.userservice.dto.LoginResponse;
import com.sporttracker.userservice.dto.RegisterRequest;
import com.sporttracker.userservice.model.User;

public interface UserService {
    User register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}