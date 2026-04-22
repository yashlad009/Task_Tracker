package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MilestoneRepository extends JpaRepository<Milestone, String> {
    // This allows the Analytics and Dashboard to find specific user milestones
    List<Milestone> findByUserId(String userId);
}
