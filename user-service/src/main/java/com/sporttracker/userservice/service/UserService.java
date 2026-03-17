package com.sporttracker.userservice.service;

import com.sporttracker.userservice.dto.RegisterRequest;
import com.sporttracker.userservice.model.User;

public interface UserService {
    User register(RegisterRequest request);

}