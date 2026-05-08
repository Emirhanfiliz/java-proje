package com.sporttracker.shared.pattern.factory;

import com.sporttracker.shared.pattern.strategy.CalorieCalculationStrategy;
import com.sporttracker.shared.pattern.strategy.DefaultIntensityStrategy;
import com.sporttracker.shared.pattern.strategy.HighIntensityStrategy;
import com.sporttracker.shared.pattern.strategy.LowIntensityStrategy;
import com.sporttracker.shared.pattern.strategy.MediumIntensityStrategy;

/**
 * Factory class for creating the appropriate CalorieCalculationStrategy.
 */
public class CalorieStrategyFactory {

    private CalorieStrategyFactory() {}

    public static CalorieCalculationStrategy getStrategy(String intensity) {
        if (intensity == null) {
            return new DefaultIntensityStrategy();
        }

        String lowerIntensity = intensity.toLowerCase();
        if (lowerIntensity.contains("low") || lowerIntensity.contains("düşük")) {
            return new LowIntensityStrategy();
        } else if (lowerIntensity.contains("medium") || lowerIntensity.contains("orta")) {
            return new MediumIntensityStrategy();
        } else if (lowerIntensity.contains("high") || lowerIntensity.contains("yüksek")) {
            return new HighIntensityStrategy();
        }

        return new DefaultIntensityStrategy();
    }
}
