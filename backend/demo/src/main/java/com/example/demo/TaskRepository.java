package com.example.demo;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

/**
 * MongoDB repository for task operations.
 */
public interface TaskRepository extends MongoRepository<Task, String> {

    // Used to fetch all tasks for a specific user on the Dashboard
    List<Task> findByUserId(String userId);

    // Used by AnalyticsController to count 'Completed' vs 'Pending'
    long countByUserIdAndStatus(String userId, String status);

    // Used by AnalyticsController to calculate the total tasks for efficiency
    long countByUserId(String userId);
}
