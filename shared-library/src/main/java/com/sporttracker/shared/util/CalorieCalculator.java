package com.sporttracker.shared.util;

import com.sporttracker.shared.pattern.factory.CalorieStrategyFactory;
import com.sporttracker.shared.pattern.strategy.CalorieCalculationStrategy;

public class CalorieCalculator {

    private CalorieCalculator() {}

    public static int calculateCalories(int durationMinutes, String intensity) {
        if (durationMinutes <= 0) return 0;

        CalorieCalculationStrategy strategy = CalorieStrategyFactory.getStrategy(intensity);
        return strategy.calculate(durationMinutes);
    }
}
