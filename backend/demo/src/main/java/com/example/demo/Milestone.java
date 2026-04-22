package com.example.demo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * MongoDB Version of the Milestone Entity.
 * Maps this class to the "milestones" collection in your MongoDB database.
 */
@Entity
@Table(name = "milestones")
public class Milestone {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;
    private int progress;
    private boolean celebrated;

    // This connects the milestone to the User's String ID
    @Column(name = "user_id", nullable = false)
    private String userId;

    // Default constructor is mandatory for MongoDB mapping
    public Milestone() {}

    @PrePersist
    public void ensureId() {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
    }

    // Getters and Setters
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
