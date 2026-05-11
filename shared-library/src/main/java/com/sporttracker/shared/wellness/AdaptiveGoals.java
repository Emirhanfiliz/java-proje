package com.sporttracker.shared.wellness;

import java.io.Serializable;

public class AdaptiveGoals implements Serializable {

    private static final long serialVersionUID = 1L;

    private int weeklyWorkoutGoal = 4;
    private int weeklyMinutesGoal = 240;
    private int dailyStepsGoal = 10000;
    private int dailyWaterGoal = 8;
    private int dailyCaloriesGoal = 500;
    private boolean autoAdjustEnabled = true;
    private String lastAdjustedAt;
    private String lastAdjustmentNote;

    public AdaptiveGoals() {}

    public int getWeeklyWorkoutGoal() { return weeklyWorkoutGoal; }
    public void setWeeklyWorkoutGoal(int weeklyWorkoutGoal) { this.weeklyWorkoutGoal = weeklyWorkoutGoal; }

    public int getWeeklyMinutesGoal() { return weeklyMinutesGoal; }
    public void setWeeklyMinutesGoal(int weeklyMinutesGoal) { this.weeklyMinutesGoal = weeklyMinutesGoal; }

    public int getDailyStepsGoal() { return dailyStepsGoal; }
    public void setDailyStepsGoal(int dailyStepsGoal) { this.dailyStepsGoal = dailyStepsGoal; }

    public int getDailyWaterGoal() { return dailyWaterGoal; }
    public void setDailyWaterGoal(int dailyWaterGoal) { this.dailyWaterGoal = dailyWaterGoal; }

    public int getDailyCaloriesGoal() { return dailyCaloriesGoal; }
    public void setDailyCaloriesGoal(int dailyCaloriesGoal) { this.dailyCaloriesGoal = dailyCaloriesGoal; }

    public boolean isAutoAdjustEnabled() { return autoAdjustEnabled; }
    public void setAutoAdjustEnabled(boolean autoAdjustEnabled) { this.autoAdjustEnabled = autoAdjustEnabled; }

    public String getLastAdjustedAt() { return lastAdjustedAt; }
    public void setLastAdjustedAt(String lastAdjustedAt) { this.lastAdjustedAt = lastAdjustedAt; }

    public String getLastAdjustmentNote() { return lastAdjustmentNote; }
    public void setLastAdjustmentNote(String lastAdjustmentNote) { this.lastAdjustmentNote = lastAdjustmentNote; }
}
