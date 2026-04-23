package com.example.demo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Task entity.
 */
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "status", nullable = false)
    private String status = "Pending";

    @Column(name = "user_id", nullable = false)
    private String userId;

    public Task() {}

    // Getters and Setters
    @PrePersist
    public void ensureId() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "category", nullable = false)
    private String category = "Personal"; // study, health, entertainment, work, etc.

    // Add Getter and Setter
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
