package com.sporttracker.shared.pattern.strategy;

public class MediumIntensityStrategy implements CalorieCalculationStrategy {
    private static final int KCAL_PER_MINUTE = 7;

    @Override
    public int calculate(int durationMinutes) {
        return durationMinutes * KCAL_PER_MINUTE;
    }
}
