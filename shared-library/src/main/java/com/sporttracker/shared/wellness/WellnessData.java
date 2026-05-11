package com.sporttracker.shared.wellness;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Root persistence container for all wellness features.
 * Stored as JSON per-user under the user's home directory.
 */
public class WellnessData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userKey;
    private String updatedAt;

    private WeeklyWorkoutPlan weeklyPlan;
    private AdaptiveGoals adaptiveGoals = new AdaptiveGoals();
    private List<RecoveryEntry> recoveryHistory = new ArrayList<>();
    private List<HrZoneSession> hrZoneSessions = new ArrayList<>();
    private List<PrRecord> personalRecords = new ArrayList<>();
    private List<Habit> habits = new ArrayList<>();
    private List<Reminder> reminders = new ArrayList<>();
    private List<MealEntry> meals = new ArrayList<>();
    private List<Challenge> challenges = new ArrayList<>();
    private List<String> earnedBadges = new ArrayList<>();

    public WellnessData() {}

    public String getUserKey() { return userKey; }
    public void setUserKey(String userKey) { this.userKey = userKey; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public WeeklyWorkoutPlan getWeeklyPlan() { return weeklyPlan; }
    public void setWeeklyPlan(WeeklyWorkoutPlan weeklyPlan) { this.weeklyPlan = weeklyPlan; }

    public AdaptiveGoals getAdaptiveGoals() {
        if (adaptiveGoals == null) adaptiveGoals = new AdaptiveGoals();
        return adaptiveGoals;
    }

    public void setAdaptiveGoals(AdaptiveGoals adaptiveGoals) { this.adaptiveGoals = adaptiveGoals; }

    public List<RecoveryEntry> getRecoveryHistory() {
        if (recoveryHistory == null) recoveryHistory = new ArrayList<>();
        return recoveryHistory;
    }

    public void setRecoveryHistory(List<RecoveryEntry> recoveryHistory) {
        this.recoveryHistory = recoveryHistory != null ? recoveryHistory : new ArrayList<>();
    }

    public List<HrZoneSession> getHrZoneSessions() {
        if (hrZoneSessions == null) hrZoneSessions = new ArrayList<>();
        return hrZoneSessions;
    }

    public void setHrZoneSessions(List<HrZoneSession> hrZoneSessions) {
        this.hrZoneSessions = hrZoneSessions != null ? hrZoneSessions : new ArrayList<>();
    }

    public List<PrRecord> getPersonalRecords() {
        if (personalRecords == null) personalRecords = new ArrayList<>();
        return personalRecords;
    }

    public void setPersonalRecords(List<PrRecord> personalRecords) {
        this.personalRecords = personalRecords != null ? personalRecords : new ArrayList<>();
    }

    public List<Habit> getHabits() {
        if (habits == null) habits = new ArrayList<>();
        return habits;
    }

    public void setHabits(List<Habit> habits) {
        this.habits = habits != null ? habits : new ArrayList<>();
    }

    public List<Reminder> getReminders() {
        if (reminders == null) reminders = new ArrayList<>();
        return reminders;
    }

    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders != null ? reminders : new ArrayList<>();
    }

    public List<MealEntry> getMeals() {
        if (meals == null) meals = new ArrayList<>();
        return meals;
    }

    public void setMeals(List<MealEntry> meals) {
        this.meals = meals != null ? meals : new ArrayList<>();
    }

    public List<Challenge> getChallenges() {
        if (challenges == null) challenges = new ArrayList<>();
        return challenges;
    }

    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges != null ? challenges : new ArrayList<>();
    }

    public List<String> getEarnedBadges() {
        if (earnedBadges == null) earnedBadges = new ArrayList<>();
        return earnedBadges;
    }

    public void setEarnedBadges(List<String> earnedBadges) {
        this.earnedBadges = earnedBadges != null ? earnedBadges : new ArrayList<>();
    }
}
