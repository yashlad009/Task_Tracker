package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    // Used by AuthController for login and duplicate checks
    Optional<User> findByEmail(String email);
}
