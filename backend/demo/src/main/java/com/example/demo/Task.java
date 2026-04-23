package com.example.demo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB task document.
 */
@Document(collection = "tasks")
public class Task {

    @Id
    private String id;

    private String text;

    private String status = "Pending";

    private String userId;

    public Task() {}

    // Getters and Setters
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

    private String category = "Personal"; // study, health, entertainment, work, etc.

    // Add Getter and Setter
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
