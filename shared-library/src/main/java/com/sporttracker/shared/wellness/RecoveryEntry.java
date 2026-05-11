package com.sporttracker.shared.wellness;

import java.io.Serializable;

public class RecoveryEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    private String date;
    private double sleepHours;
    private int restingHeartRate;
    private int trainingLoad;
    private int score;
    private String label;

    public RecoveryEntry() {}

    public RecoveryEntry(String date, double sleepHours, int restingHeartRate, int trainingLoad) {
        this.date = date;
        this.sleepHours = sleepHours;
        this.restingHeartRate = restingHeartRate;
        this.trainingLoad = trainingLoad;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getSleepHours() { return sleepHours; }
    public void setSleepHours(double sleepHours) { this.sleepHours = sleepHours; }

    public int getRestingHeartRate() { return restingHeartRate; }
    public void setRestingHeartRate(int restingHeartRate) { this.restingHeartRate = restingHeartRate; }

    public int getTrainingLoad() { return trainingLoad; }
    public void setTrainingLoad(int trainingLoad) { this.trainingLoad = trainingLoad; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
