package com.sporttracker.shared.wellness;

import java.io.Serializable;

public class TrendInsight implements Serializable {

    private static final long serialVersionUID = 1L;

    private String period;
    private int totalWorkouts;
    private int totalMinutes;
    private int totalCalories;
    private double averageRecoveryScore;
    private double trendPercentage;
    private String advice;
    private String motivation;

    public TrendInsight() {}

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public int getTotalWorkouts() { return totalWorkouts; }
    public void setTotalWorkouts(int totalWorkouts) { this.totalWorkouts = totalWorkouts; }

    public int getTotalMinutes() { return totalMinutes; }
    public void setTotalMinutes(int totalMinutes) { this.totalMinutes = totalMinutes; }

    public int getTotalCalories() { return totalCalories; }
    public void setTotalCalories(int totalCalories) { this.totalCalories = totalCalories; }

    public double getAverageRecoveryScore() { return averageRecoveryScore; }
    public void setAverageRecoveryScore(double averageRecoveryScore) { this.averageRecoveryScore = averageRecoveryScore; }

    public double getTrendPercentage() { return trendPercentage; }
    public void setTrendPercentage(double trendPercentage) { this.trendPercentage = trendPercentage; }

    public String getAdvice() { return advice; }
    public void setAdvice(String advice) { this.advice = advice; }

    public String getMotivation() { return motivation; }
    public void setMotivation(String motivation) { this.motivation = motivation; }
}
