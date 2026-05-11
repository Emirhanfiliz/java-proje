package com.sporttracker.mobile.wellness;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sporttracker.mobile.session.MobileSessionManager;
import com.sporttracker.shared.wellness.ChallengeEngine;
import com.sporttracker.shared.wellness.HabitEngine;
import com.sporttracker.shared.wellness.WeeklyWorkoutPlan;
import com.sporttracker.shared.wellness.WellnessData;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

/**
 * Local JSON-backed persistence for wellness features on the mobile client.
 */
public final class WellnessStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Object LOCK = new Object();

    private static WellnessData cached;
    private static String cachedKey;

    private WellnessStore() {}

    public static WellnessData load() {
        synchronized (LOCK) {
            String key = currentUserKey();
            if (cached != null && key.equals(cachedKey)) {
                return cached;
            }
            cachedKey = key;
            Path file = storeFile(key);
            if (Files.exists(file)) {
                try {
                    String json = Files.readString(file, StandardCharsets.UTF_8);
                    WellnessData data = GSON.fromJson(json, WellnessData.class);
                    if (data == null) data = new WellnessData();
                    cached = postProcess(data, key);
                    return cached;
                } catch (IOException | RuntimeException ex) {
                    cached = postProcess(new WellnessData(), key);
                    return cached;
                }
            }
            cached = postProcess(new WellnessData(), key);
            save(cached);
            return cached;
        }
    }

    public static void save(WellnessData data) {
        if (data == null) return;
        synchronized (LOCK) {
            String key = currentUserKey();
            data.setUserKey(key);
            data.setUpdatedAt(LocalDateTime.now().toString());
            cached = data;
            cachedKey = key;
            try {
                Path file = storeFile(key);
                Files.createDirectories(file.getParent());
                Files.writeString(file, GSON.toJson(data), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                // Soft-fail to keep UI usable in current session
            }
        }
    }

    public static void clearCache() {
        synchronized (LOCK) {
            cached = null;
            cachedKey = null;
        }
    }

    private static WellnessData postProcess(WellnessData data, String key) {
        data.setUserKey(key);
        if (data.getWeeklyPlan() == null) {
            data.setWeeklyPlan(WeeklyWorkoutPlan.buildPushPullLegs());
        }
        if (data.getAdaptiveGoals() == null
                || data.getAdaptiveGoals().getWeeklyMinutesGoal() <= 0) {
            data.getAdaptiveGoals().setWeeklyMinutesGoal(240);
            data.getAdaptiveGoals().setWeeklyWorkoutGoal(4);
            data.getAdaptiveGoals().setDailyStepsGoal(10000);
            data.getAdaptiveGoals().setDailyWaterGoal(8);
        }
        HabitEngine.ensureDefaults(data);
        ChallengeEngine.ensureWeeklyChallenges(data);
        return data;
    }

    private static String currentUserKey() {
        String userId = MobileSessionManager.getInstance().getUserId();
        if (userId != null && !userId.isBlank()) return sanitize(userId);
        String username = MobileSessionManager.getInstance().getUsername();
        if (username != null && !username.isBlank()) return sanitize(username);
        return "guest";
    }

    private static String sanitize(String value) {
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private static Path storeFile(String key) {
        String home = System.getProperty("user.home", ".");
        return Paths.get(home, ".sporttracker", "mobile", "wellness-" + key + ".json");
    }
}
