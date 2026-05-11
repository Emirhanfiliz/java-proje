package com.sporttracker.shared.wellness;

import java.io.Serializable;

public class PlannedExercise implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private int sets;
    private int reps;
    private double weightKg;
    private String muscleGroup;

    public PlannedExercise() {}

    public PlannedExercise(String name, int sets, int reps, double weightKg, String muscleGroup) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.weightKg = weightKg;
        this.muscleGroup = muscleGroup;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }

    public String getMuscleGroup() { return muscleGroup; }
    public void setMuscleGroup(String muscleGroup) { this.muscleGroup = muscleGroup; }

    @Override
    public String toString() {
        return String.format("%s — %dx%d @ %.1fkg", name, sets, reps, weightKg);
    }
}
