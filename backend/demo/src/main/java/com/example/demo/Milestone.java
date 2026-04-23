package com.example.demo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Milestone entity.
 */
@Entity
@Table(name = "milestones")
public class Milestone {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "progress", nullable = false)
    private int progress;

    @Column(name = "celebrated", nullable = false)
    private boolean celebrated;

    @Column(name = "user_id", nullable = false)
    private String userId;

    public Milestone() {}

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isCelebrated() {
        return celebrated;
    }

    public void setCelebrated(boolean celebrated) {
        this.celebrated = celebrated;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
