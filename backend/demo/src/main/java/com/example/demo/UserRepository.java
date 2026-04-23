package com.example.demo;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    // Used by AuthController for login and duplicate checks
    Optional<User> findByEmail(String email);
}
