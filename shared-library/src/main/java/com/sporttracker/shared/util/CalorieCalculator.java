package com.sporttracker.shared.util;

public class CalorieCalculator {

    private CalorieCalculator() {}

    public static int calculateCalories(int durationMinutes, String intensity) {
        if (durationMinutes <= 0) return 0;

        int kcalPerMinute = 5;
        if (intensity != null) {
            String lowerIntensity = intensity.toLowerCase();
            if (lowerIntensity.contains("low") || lowerIntensity.contains("düşük")) {
                kcalPerMinute = 4;
            } else if (lowerIntensity.contains("medium") || lowerIntensity.contains("orta")) {
                kcalPerMinute = 7;
            } else if (lowerIntensity.contains("high") || lowerIntensity.contains("yüksek")) {
                kcalPerMinute = 10;
            }
        }
        
        return durationMinutes * kcalPerMinute;
    }
}
