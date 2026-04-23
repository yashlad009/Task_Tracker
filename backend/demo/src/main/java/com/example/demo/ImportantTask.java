package com.example.demo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "important_tasks")
public class ImportantTask {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "task_name", nullable = false)
    private String taskName;

    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime; // The time user picked

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "processed", nullable = false)
    private boolean processed = false; // To ensure we don't send the email twice

    // Getters and Setters
    @PrePersist
    public void ensureId() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public boolean isProcessed() { return processed; }
    public void setProcessed(boolean processed) { this.processed = processed; }
}
