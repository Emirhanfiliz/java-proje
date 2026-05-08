package com.sporttracker.userservice.cache;

import com.sporttracker.userservice.model.User;
import com.sporttracker.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCacheService {

    private final UserRepository userRepository;

    @Cacheable(value = "users", key = "#email", unless = "#result == null")
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @CacheEvict(value = "users", key = "#email")
    public void evict(String email) {
    }
}
