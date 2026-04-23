package com.example.demo;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface UserQueryRepository extends MongoRepository<UserQuery, String> {
    List<UserQuery> findByUserEmail(String userEmail);
}
