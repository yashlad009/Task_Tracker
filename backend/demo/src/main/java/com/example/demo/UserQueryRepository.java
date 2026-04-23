package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserQueryRepository extends JpaRepository<UserQuery, String> {
    List<UserQuery> findByUserEmail(String userEmail);
}
