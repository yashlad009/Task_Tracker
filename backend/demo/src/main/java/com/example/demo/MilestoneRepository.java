package com.example.demo;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MilestoneRepository extends MongoRepository<Milestone, String> {
    // This allows the Analytics and Dashboard to find specific user milestones
    List<Milestone> findByUserId(String userId);
}
