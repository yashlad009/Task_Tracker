package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * MongoDB Repository for Task operations.
 * Extends MongoRepository to handle String-based ObjectIDs.
 */
public interface TaskRepository extends JpaRepository<Task, String> {

    // Used to fetch all tasks for a specific user on the Dashboard
    List<Task> findByUserId(String userId);

    // Used by AnalyticsController to count 'Completed' vs 'Pending'
    long countByUserIdAndStatus(String userId, String status);

    // Used by AnalyticsController to calculate the total tasks for efficiency
    long countByUserId(String userId);
}
