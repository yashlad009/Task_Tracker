package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
@CrossOrigin(origins = "*") // Crucial for your Dashboard charts to fetch data
public class AnalyticsController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GamificationService gamificationService;

    /**
     * Fetch user-specific stats for the Analytics Dashboard.
     * Uses String identifiers across the API contract.
     */
    @GetMapping("/user/{userId}")
    @Transactional(readOnly = true)
    public Map<String, Object> getUserStats(@PathVariable String userId) {

        // 1. Fetch counts from TaskRepository
        long completed = taskRepository.countByUserIdAndStatus(userId, "Completed");
        long pending = taskRepository.countByUserIdAndStatus(userId, "Pending");
        long total = taskRepository.countByUserId(userId);

        // 2. Calculate efficiency percentage (Double check for total > 0 to avoid /0)
        int efficiency = (total > 0) ? (int) Math.round(((double) completed / total) * 100) : 0;
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put("completed", completed);
        response.put("pending", pending);
        response.put("total", total);
        response.put("efficiency", efficiency);

        userRepository.findById(userId).ifPresent(user -> response.put("gamification", gamificationService.buildGamificationSummary(user)));
        return response;
    }
}
