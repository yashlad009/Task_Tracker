package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GamificationService gamificationService;

    @PostMapping("/add")
    public Task addTask(@RequestBody Task task) {
        // Default values for new categorized tasks
        if (task.getStatus() == null || task.getStatus().isEmpty()) {
            task.setStatus("Pending");
        }
        if (task.getCategory() == null || task.getCategory().isEmpty()) {
            task.setCategory("Personal");
        }
        return taskRepository.save(task);
    }

    @GetMapping("/user/{userId}")
    public List<Task> getTasksByUser(@PathVariable String userId) {
        return taskRepository.findByUserId(userId);
    }

    @GetMapping("/all")
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeTask(@PathVariable String id) {
        return taskRepository.findById(id).map(task -> {
            LinkedHashMap<String, Object> response = new LinkedHashMap<>();

            if ("Completed".equalsIgnoreCase(task.getStatus())) {
                response.put("task", task);
                response.put("tokensAwarded", 0);
                userRepository.findById(task.getUserId()).ifPresent(user -> {
                    gamificationService.ensureInitialized(user);
                    response.put("user", user);
                    response.put("gamification", gamificationService.buildGamificationSummary(user));
                });
                response.put("newlyUnlocked", List.of());
                return ResponseEntity.ok(response);
            }

            task.setStatus("Completed");
            taskRepository.save(task);

            response.put("task", task);
            response.put("tokensAwarded", gamificationService.getTokenValueForCategory(task.getCategory()));

            userRepository.findById(task.getUserId()).ifPresent(user -> {
                List<java.util.Map<String, Object>> newlyUnlocked = gamificationService.awardTaskCompletion(user, task);
                userRepository.save(user);
                response.put("user", user);
                response.put("gamification", gamificationService.buildGamificationSummary(user));
                response.put("newlyUnlocked", newlyUnlocked);
            });

            response.putIfAbsent("newlyUnlocked", List.of());
            return ResponseEntity.ok(response);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable String id) {
        return taskRepository.findById(id).map(task -> {
            taskRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
