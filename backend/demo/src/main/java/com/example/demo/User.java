package com.example.demo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MongoDB user document with role-based access control data.
 */
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String password;

    /**
     * The role field determines system privileges.
     * Values: "USER" or "ADMIN"
     */
    private String role = "USER";
    private int tokens = 0;
    private List<String> unlockedRewards = new ArrayList<>();
    private Map<String, String> rewardUnlockedDates = new HashMap<>();

    public User() {}

    // Updated constructor to include role initialization
    public User(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getTokens() {
        return tokens;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }

    public List<String> getUnlockedRewards() {
        if (unlockedRewards == null) {
            unlockedRewards = new ArrayList<>();
        }
        return unlockedRewards;
    }

    public void setUnlockedRewards(List<String> unlockedRewards) {
        this.unlockedRewards = unlockedRewards;
    }

    public Map<String, String> getRewardUnlockedDates() {
        if (rewardUnlockedDates == null) {
            rewardUnlockedDates = new HashMap<>();
        }
        return rewardUnlockedDates;
    }

    public void setRewardUnlockedDates(Map<String, String> rewardUnlockedDates) {
        this.rewardUnlockedDates = rewardUnlockedDates;
    }
}
