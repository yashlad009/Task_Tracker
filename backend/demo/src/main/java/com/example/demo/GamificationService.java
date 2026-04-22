package com.example.demo;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class GamificationService {

    private static final LinkedHashMap<String, Integer> CATEGORY_TOKENS = new LinkedHashMap<>();
    private static final List<RewardTier> REWARD_TIERS = List.of(
            new RewardTier("Bronze", "Bronze Certificate", "\uD83E\uDD49", 100),
            new RewardTier("Silver", "Silver Certificate", "\uD83E\uDD48", 200),
            new RewardTier("Gold", "Gold Certificate", "\uD83E\uDD47", 500),
            new RewardTier("Elite 🚀", "Elite Badge", "\uD83D\uDD25", 1000)
    );

    static {
        CATEGORY_TOKENS.put("Study", 10);
        CATEGORY_TOKENS.put("Health", 8);
        CATEGORY_TOKENS.put("Work", 12);
        CATEGORY_TOKENS.put("Personal", 6);
        CATEGORY_TOKENS.put("Entertainment", 6);
    }

    public void ensureInitialized(User user) {
        if (user.getUnlockedRewards() == null) {
            user.setUnlockedRewards(new ArrayList<>());
        }
        if (user.getRewardUnlockedDates() == null) {
            user.setRewardUnlockedDates(new LinkedHashMap<>());
        }
    }

    public int getTokenValueForCategory(String category) {
        return CATEGORY_TOKENS.getOrDefault(category, 6);
    }

    public List<Map<String, Object>> awardTaskCompletion(User user, Task task) {
        ensureInitialized(user);
        int awardedTokens = getTokenValueForCategory(task.getCategory());
        user.setTokens(user.getTokens() + awardedTokens);

        List<Map<String, Object>> newlyUnlocked = new ArrayList<>();
        for (RewardTier tier : REWARD_TIERS) {
            if (user.getTokens() >= tier.threshold() && !user.getUnlockedRewards().contains(tier.key())) {
                user.getUnlockedRewards().add(tier.key());
                user.getRewardUnlockedDates().put(tier.key(), LocalDate.now().toString());
                newlyUnlocked.add(buildRewardMap(user, tier));
            }
        }

        return newlyUnlocked;
    }

    public Map<String, Object> buildGamificationSummary(User user) {
        ensureInitialized(user);

        LinkedHashMap<String, Object> summary = new LinkedHashMap<>();
        summary.put("tokens", user.getTokens());
        summary.put("level", Math.max(1, (user.getTokens() / 100) + 1));
        summary.put("categoryTokens", CATEGORY_TOKENS);

        List<Map<String, Object>> achievements = new ArrayList<>();
        RewardTier nextReward = null;
        int previousThreshold = 0;

        for (RewardTier tier : REWARD_TIERS) {
            boolean unlocked = user.getUnlockedRewards().contains(tier.key());
            if (!unlocked && nextReward == null) {
                nextReward = tier;
            }

            if (unlocked) {
                previousThreshold = tier.threshold();
            }

            achievements.add(buildRewardMap(user, tier));
        }

        if (nextReward != null) {
            int currentProgress = Math.max(0, user.getTokens() - previousThreshold);
            int targetProgress = nextReward.threshold() - previousThreshold;
            int progressPercent = targetProgress > 0
                    ? (int) Math.round(((double) currentProgress / targetProgress) * 100)
                    : 100;

            summary.put("nextReward", buildRewardMap(user, nextReward));
            summary.put("progressCurrent", user.getTokens());
            summary.put("progressTarget", nextReward.threshold());
            summary.put("progressPercent", Math.min(progressPercent, 100));
            summary.put("progressWithinTier", currentProgress);
            summary.put("progressWithinTierTarget", targetProgress);
        } else {
            summary.put("nextReward", null);
            summary.put("progressCurrent", user.getTokens());
            summary.put("progressTarget", user.getTokens());
            summary.put("progressPercent", 100);
            summary.put("progressWithinTier", 0);
            summary.put("progressWithinTierTarget", 0);
        }

        summary.put("achievements", achievements);
        return summary;
    }

    private Map<String, Object> buildRewardMap(User user, RewardTier tier) {
        LinkedHashMap<String, Object> reward = new LinkedHashMap<>();
        boolean unlocked = user.getUnlockedRewards().contains(tier.key());

        reward.put("key", tier.key());
        reward.put("name", tier.name());
        reward.put("icon", tier.icon());
        reward.put("threshold", tier.threshold());
        reward.put("unlocked", unlocked);
        reward.put("unlockedDate", user.getRewardUnlockedDates().get(tier.key()));
        return reward;
    }

    private static class RewardTier {
        private final String key;
        private final String name;
        private final String icon;
        private final int threshold;

        private RewardTier(String key, String name, String icon, int threshold) {
            this.key = key;
            this.name = name;
            this.icon = icon;
            this.threshold = threshold;
        }

        public String key() {
            return key;
        }

        public String name() {
            return name;
        }

        public String icon() {
            return icon;
        }

        public int threshold() {
            return threshold;
        }
    }
}
