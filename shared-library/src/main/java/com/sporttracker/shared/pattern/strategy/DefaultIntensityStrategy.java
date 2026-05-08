package com.sporttracker.shared.pattern.strategy;

public class DefaultIntensityStrategy implements CalorieCalculationStrategy {
    private static final int KCAL_PER_MINUTE = 5;

    @Override
    public int calculate(int durationMinutes) {
        return durationMinutes * KCAL_PER_MINUTE;
    }
}
