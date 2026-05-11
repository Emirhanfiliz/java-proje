package com.sporttracker.shared.wellness;

import java.time.LocalDate;
import java.util.List;

/**
 * Aggregates macro totals for meal snapshots.
 */
public final class MealAggregator {

    private MealAggregator() {}

    public static class MacroTotals {
        public int calories;
        public double protein;
        public double carbs;
        public double fat;
        public int mealCount;
    }

    public static MacroTotals totalsForDate(List<MealEntry> meals, String isoDate) {
        MacroTotals totals = new MacroTotals();
        if (meals == null || isoDate == null) return totals;
        for (MealEntry meal : meals) {
            if (isoDate.equals(meal.getDate())) {
                totals.calories += meal.getCalories();
                totals.protein += meal.getProteinGrams();
                totals.carbs += meal.getCarbsGrams();
                totals.fat += meal.getFatGrams();
                totals.mealCount++;
            }
        }
        return totals;
    }

    public static MacroTotals totalsForToday(List<MealEntry> meals) {
        return totalsForDate(meals, LocalDate.now().toString());
    }

    public static MacroTotals totalsForLastDays(List<MealEntry> meals, int days) {
        MacroTotals totals = new MacroTotals();
        if (meals == null) return totals;
        LocalDate today = LocalDate.now();
        LocalDate cutoff = today.minusDays(Math.max(1, days) - 1);
        for (MealEntry meal : meals) {
            if (meal.getDate() == null) continue;
            try {
                LocalDate date = LocalDate.parse(meal.getDate().length() > 10 ? meal.getDate().substring(0, 10) : meal.getDate());
                if (!date.isBefore(cutoff) && !date.isAfter(today)) {
                    totals.calories += meal.getCalories();
                    totals.protein += meal.getProteinGrams();
                    totals.carbs += meal.getCarbsGrams();
                    totals.fat += meal.getFatGrams();
                    totals.mealCount++;
                }
            } catch (Exception ignored) {
            }
        }
        return totals;
    }
}
