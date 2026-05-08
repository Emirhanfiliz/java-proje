package com.sporttracker.mobile.util;

/**
 * Basit Kalori Hesaplama Mantığı
 */
public class CalorieCalculator {

    private CalorieCalculator() {}

    /**
     * Süre (dakika) ve şiddete göre tahmini kalori yakımı hesaplar.
     * Düşük şiddet = ~4 kcal / dk
     * Orta şiddet  = ~7 kcal / dk
     * Yüksek şiddet= ~10 kcal / dk
     */
    public static int calculateCalories(int durationMinutes, String intensity) {
        if (durationMinutes <= 0) return 0;

        int kcalPerMinute = 5; // default
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
