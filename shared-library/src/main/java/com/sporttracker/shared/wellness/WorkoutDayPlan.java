package com.sporttracker.shared.wellness;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkoutDayPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dayOfWeek;
    private String splitType;
    private String focus;
    private boolean restDay;
    private List<PlannedExercise> exercises = new ArrayList<>();

    public WorkoutDayPlan() {}

    public WorkoutDayPlan(String dayOfWeek, String splitType, String focus, boolean restDay) {
        this.dayOfWeek = dayOfWeek;
        this.splitType = splitType;
        this.focus = focus;
        this.restDay = restDay;
    }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getSplitType() { return splitType; }
    public void setSplitType(String splitType) { this.splitType = splitType; }

    public String getFocus() { return focus; }
    public void setFocus(String focus) { this.focus = focus; }

    public boolean isRestDay() { return restDay; }
    public void setRestDay(boolean restDay) { this.restDay = restDay; }

    public List<PlannedExercise> getExercises() {
        if (exercises == null) {
            exercises = new ArrayList<>();
        }
        return exercises;
    }

    public void setExercises(List<PlannedExercise> exercises) {
        this.exercises = exercises != null ? exercises : new ArrayList<>();
    }
}
