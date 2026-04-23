package com.example.demo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_queries")
public class UserQuery {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "admin_reply", columnDefinition = "TEXT")
    private String adminReply; // Initially null

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "resolved", nullable = false)
    private boolean resolved;

    public UserQuery() { this.timestamp = LocalDateTime.now(); this.resolved = false; }

    // Getters and Setters
    @PrePersist
    public void ensureDefaults() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getAdminReply() { return adminReply; }
    public void setAdminReply(String adminReply) { this.adminReply = adminReply; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }
}
