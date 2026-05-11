package com.sporttracker.shared.wellness;

import java.io.Serializable;

public class PrRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private String exerciseName;
    private double weightKg;
    private int reps;
    private String date;
    private String notes;

    public PrRecord() {}

    public PrRecord(String exerciseName, double weightKg, int reps, String date, String notes) {
        this.exerciseName = exerciseName;
        this.weightKg = weightKg;
        this.reps = reps;
        this.date = date;
        this.notes = notes;
    }

    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    /**
     * Estimates 1RM using the Epley formula.
     */
    public double estimatedOneRepMax() {
        if (reps <= 0) return weightKg;
        if (reps == 1) return weightKg;
        return weightKg * (1.0 + reps / 30.0);
    }
}
