package com.example.demo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public final class UserResponseMapper {

    private UserResponseMapper() {
    }

    public static Map<String, Object> toUserResponse(User user) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put("id", user.getId());
        response.put("_id", user.getId());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("tokens", user.getTokens());
        response.put("unlockedRewards", new ArrayList<>(user.getUnlockedRewards()));
        response.put("rewardUnlockedDates", new LinkedHashMap<>(user.getRewardUnlockedDates()));
        return response;
    }
}
