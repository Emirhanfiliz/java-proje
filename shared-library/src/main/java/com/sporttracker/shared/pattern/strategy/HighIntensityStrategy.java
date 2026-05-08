package com.sporttracker.shared.pattern.strategy;

public class HighIntensityStrategy implements CalorieCalculationStrategy {
    private static final int KCAL_PER_MINUTE = 10;

    @Override
    public int calculate(int durationMinutes) {
        return durationMinutes * KCAL_PER_MINUTE;
    }
}
