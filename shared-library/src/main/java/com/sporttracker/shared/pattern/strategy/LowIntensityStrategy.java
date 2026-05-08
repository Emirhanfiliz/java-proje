package com.sporttracker.shared.pattern.strategy;

public class LowIntensityStrategy implements CalorieCalculationStrategy {
    private static final int KCAL_PER_MINUTE = 4;

    @Override
    public int calculate(int durationMinutes) {
        return durationMinutes * KCAL_PER_MINUTE;
    }
}
