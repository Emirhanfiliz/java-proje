package com.sporttracker.shared.pattern.strategy;

/**
 * Strategy interface for calculating calories based on different intensity levels.
 */
public interface CalorieCalculationStrategy {
    int calculate(int durationMinutes);
}
