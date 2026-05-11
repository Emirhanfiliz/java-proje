package com.sporttracker.shared.wellness;

import java.time.LocalDate;
import java.util.List;

/**
 * Adjusts daily / weekly goals based on actual completion rates.
 */
public final class AdaptiveGoalEngine {

    private AdaptiveGoalEngine() {}

    /**
     * Adjusts the goals in {@link WellnessData#getAdaptiveGoals()} using the supplied weekly stats.
     * Goals only change when {@link AdaptiveGoals#isAutoAdjustEnabled()} returns {@code true}.
     */
    public static AdaptiveGoals adjust(WellnessData data,
                                       int completedWorkoutsThisWeek,
                                       int trainedMinutesThisWeek,
                                       int averageDailySteps,
                                       int averageDailyWater) {
        AdaptiveGoals goals = data.getAdaptiveGoals();
        if (!goals.isAutoAdjustEnabled()) return goals;

        StringBuilder note = new StringBuilder();

        int workoutGoal = goals.getWeeklyWorkoutGoal();
        int minutesGoal = goals.getWeeklyMinutesGoal();
        int stepsGoal = goals.getDailyStepsGoal();
        int waterGoal = goals.getDailyWaterGoal();

        if (completedWorkoutsThisWeek >= workoutGoal + 1) {
            workoutGoal = Math.min(7, workoutGoal + 1);
            note.append("Antrenman hedefi +1 yapıldı. ");
        } else if (completedWorkoutsThisWeek < Math.max(1, workoutGoal - 2)) {
            workoutGoal = Math.max(2, workoutGoal - 1);
            note.append("Antrenman hedefi -1 yapıldı. ");
        }

        if (trainedMinutesThisWeek >= minutesGoal * 1.10) {
            minutesGoal = Math.min(600, minutesGoal + 30);
            note.append("Haftalık dakika hedefi +30dk. ");
        } else if (trainedMinutesThisWeek < minutesGoal * 0.70) {
            minutesGoal = Math.max(120, minutesGoal - 20);
            note.append("Haftalık dakika hedefi -20dk. ");
        }

        if (averageDailySteps >= stepsGoal * 1.08) {
            stepsGoal = Math.min(20000, stepsGoal + 500);
            note.append("Adım hedefi +500. ");
        } else if (averageDailySteps > 0 && averageDailySteps < stepsGoal * 0.65) {
            stepsGoal = Math.max(4000, stepsGoal - 500);
            note.append("Adım hedefi -500. ");
        }

        if (averageDailyWater >= waterGoal) {
            waterGoal = Math.min(14, waterGoal + 1);
            note.append("Su hedefi +1 bardak. ");
        } else if (averageDailyWater > 0 && averageDailyWater < Math.max(3, waterGoal - 3)) {
            waterGoal = Math.max(4, waterGoal - 1);
            note.append("Su hedefi -1 bardak. ");
        }

        goals.setWeeklyWorkoutGoal(workoutGoal);
        goals.setWeeklyMinutesGoal(minutesGoal);
        goals.setDailyStepsGoal(stepsGoal);
        goals.setDailyWaterGoal(waterGoal);
        goals.setLastAdjustedAt(LocalDate.now().toString());
        goals.setLastAdjustmentNote(note.length() > 0 ? note.toString().trim() : "Performansın hedeflerle uyumlu, değişiklik yok.");
        return goals;
    }

    /**
     * Adjusts purely based on history kept inside the {@link WellnessData}
     * (recovery + meal entries) when no live stats are available.
     */
    public static AdaptiveGoals adjustFromHistory(WellnessData data) {
        List<RecoveryEntry> recovery = data.getRecoveryHistory();
        int avgLoad = (int) recovery.stream().mapToInt(RecoveryEntry::getTrainingLoad).average().orElse(0);
        int avgSleep = (int) Math.round(recovery.stream().mapToDouble(RecoveryEntry::getSleepHours).average().orElse(0) * 10);
        return adjust(data, Math.max(0, avgLoad / 80), Math.max(0, avgLoad / 4), Math.max(0, avgSleep * 800), data.getAdaptiveGoals().getDailyWaterGoal());
    }
}
