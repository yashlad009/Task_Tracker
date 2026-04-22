package com.example.demo;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Enhanced MongoDB User Entity with Role-Based Access Control (RBAC).
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    /**
     * The role field determines system privileges.
     * Values: "USER" or "ADMIN"
     */
    private String role;
    private int tokens = 0;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_unlocked_rewards", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "reward_key")
    private List<String> unlockedRewards = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_reward_unlocked_dates", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "reward_key")
    @Column(name = "unlocked_date")
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

    @PrePersist
    public void ensureId() {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
        if (role == null || role.isBlank()) {
            role = "USER";
        }
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
