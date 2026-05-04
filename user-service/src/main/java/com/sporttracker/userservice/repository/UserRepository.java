package com.sporttracker.userservice.repository;

import com.sporttracker.shared.repository.BaseRepository;
import com.sporttracker.userservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String>, BaseRepository<User, String> {
    Optional<User> findByEmail(String email);
}