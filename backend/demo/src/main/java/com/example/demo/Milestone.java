package com.example.demo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB milestone document.
 */
@Document(collection = "milestones")
public class Milestone {

    @Id
    private String id;

    private String name;
    private int progress;
    private boolean celebrated;

    private String userId;

    public Milestone() {}

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
