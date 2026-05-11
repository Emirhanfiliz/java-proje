package com.sporttracker.shared.wellness;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WeeklyWorkoutPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String TEMPLATE_PPL = "PPL";
    public static final String TEMPLATE_UPPER_LOWER = "UPPER_LOWER";
    public static final String TEMPLATE_FULL_BODY = "FULL_BODY";
    public static final String TEMPLATE_CUSTOM = "CUSTOM";

    private String templateName;
    private String createdAt;
    private List<WorkoutDayPlan> days = new ArrayList<>();

    public WeeklyWorkoutPlan() {}

    public WeeklyWorkoutPlan(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public List<WorkoutDayPlan> getDays() {
        if (days == null) {
            days = new ArrayList<>();
        }
        return days;
    }

    public void setDays(List<WorkoutDayPlan> days) {
        this.days = days != null ? days : new ArrayList<>();
    }

    /**
     * Builds a default Push / Pull / Legs split for 6 active days + 1 rest.
     */
    public static WeeklyWorkoutPlan buildPushPullLegs() {
        WeeklyWorkoutPlan plan = new WeeklyWorkoutPlan(TEMPLATE_PPL);
        plan.getDays().add(pushDay("Pazartesi"));
        plan.getDays().add(pullDay("Salı"));
        plan.getDays().add(legDay("Çarşamba"));
        plan.getDays().add(pushDay("Perşembe"));
        plan.getDays().add(pullDay("Cuma"));
        plan.getDays().add(legDay("Cumartesi"));
        plan.getDays().add(restDay("Pazar"));
        return plan;
    }

    public static WeeklyWorkoutPlan buildUpperLower() {
        WeeklyWorkoutPlan plan = new WeeklyWorkoutPlan(TEMPLATE_UPPER_LOWER);
        plan.getDays().add(upperDay("Pazartesi"));
        plan.getDays().add(lowerDay("Salı"));
        plan.getDays().add(restDay("Çarşamba"));
        plan.getDays().add(upperDay("Perşembe"));
        plan.getDays().add(lowerDay("Cuma"));
        plan.getDays().add(restDay("Cumartesi"));
        plan.getDays().add(restDay("Pazar"));
        return plan;
    }

    public static WeeklyWorkoutPlan buildFullBody() {
        WeeklyWorkoutPlan plan = new WeeklyWorkoutPlan(TEMPLATE_FULL_BODY);
        plan.getDays().add(fullBodyDay("Pazartesi"));
        plan.getDays().add(restDay("Salı"));
        plan.getDays().add(fullBodyDay("Çarşamba"));
        plan.getDays().add(restDay("Perşembe"));
        plan.getDays().add(fullBodyDay("Cuma"));
        plan.getDays().add(restDay("Cumartesi"));
        plan.getDays().add(restDay("Pazar"));
        return plan;
    }

    public static WeeklyWorkoutPlan emptyPlan() {
        WeeklyWorkoutPlan plan = new WeeklyWorkoutPlan(TEMPLATE_CUSTOM);
        String[] days = {"Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar"};
        for (String d : days) {
            plan.getDays().add(restDay(d));
        }
        return plan;
    }

    public static Map<String, String> templateOptions() {
        Map<String, String> opts = new LinkedHashMap<>();
        opts.put(TEMPLATE_PPL, "Push / Pull / Legs");
        opts.put(TEMPLATE_UPPER_LOWER, "Upper / Lower");
        opts.put(TEMPLATE_FULL_BODY, "Full Body");
        opts.put(TEMPLATE_CUSTOM, "Özel (Boş Plan)");
        return opts;
    }

    private static WorkoutDayPlan pushDay(String day) {
        WorkoutDayPlan d = new WorkoutDayPlan(day, "PUSH", "Göğüs / Omuz / Triceps", false);
        d.getExercises().add(new PlannedExercise("Bench Press", 4, 8, 60, "Göğüs"));
        d.getExercises().add(new PlannedExercise("Overhead Press", 3, 10, 35, "Omuz"));
        d.getExercises().add(new PlannedExercise("Incline DB Press", 3, 10, 22, "Göğüs"));
        d.getExercises().add(new PlannedExercise("Triceps Pushdown", 3, 12, 25, "Triceps"));
        return d;
    }

    private static WorkoutDayPlan pullDay(String day) {
        WorkoutDayPlan d = new WorkoutDayPlan(day, "PULL", "Sırt / Biceps", false);
        d.getExercises().add(new PlannedExercise("Deadlift", 4, 5, 100, "Sırt"));
        d.getExercises().add(new PlannedExercise("Pull-up", 4, 8, 0, "Sırt"));
        d.getExercises().add(new PlannedExercise("Barbell Row", 3, 10, 50, "Sırt"));
        d.getExercises().add(new PlannedExercise("Barbell Curl", 3, 12, 25, "Biceps"));
        return d;
    }

    private static WorkoutDayPlan legDay(String day) {
        WorkoutDayPlan d = new WorkoutDayPlan(day, "LEGS", "Bacak / Kalça", false);
        d.getExercises().add(new PlannedExercise("Squat", 4, 8, 90, "Bacak"));
        d.getExercises().add(new PlannedExercise("Romanian Deadlift", 3, 10, 70, "Hamstring"));
        d.getExercises().add(new PlannedExercise("Leg Press", 3, 12, 140, "Bacak"));
        d.getExercises().add(new PlannedExercise("Calf Raise", 4, 15, 60, "Baldır"));
        return d;
    }

    private static WorkoutDayPlan upperDay(String day) {
        WorkoutDayPlan d = new WorkoutDayPlan(day, "UPPER", "Üst Vücut", false);
        d.getExercises().add(new PlannedExercise("Bench Press", 4, 8, 60, "Göğüs"));
        d.getExercises().add(new PlannedExercise("Barbell Row", 4, 8, 55, "Sırt"));
        d.getExercises().add(new PlannedExercise("Overhead Press", 3, 10, 35, "Omuz"));
        d.getExercises().add(new PlannedExercise("Pull-up", 3, 8, 0, "Sırt"));
        return d;
    }

    private static WorkoutDayPlan lowerDay(String day) {
        WorkoutDayPlan d = new WorkoutDayPlan(day, "LOWER", "Alt Vücut", false);
        d.getExercises().add(new PlannedExercise("Squat", 4, 8, 90, "Bacak"));
        d.getExercises().add(new PlannedExercise("Romanian Deadlift", 3, 10, 70, "Hamstring"));
        d.getExercises().add(new PlannedExercise("Lunge", 3, 12, 20, "Bacak"));
        return d;
    }

    private static WorkoutDayPlan fullBodyDay(String day) {
        WorkoutDayPlan d = new WorkoutDayPlan(day, "FULL_BODY", "Tüm Vücut", false);
        d.getExercises().add(new PlannedExercise("Squat", 3, 8, 80, "Bacak"));
        d.getExercises().add(new PlannedExercise("Bench Press", 3, 8, 55, "Göğüs"));
        d.getExercises().add(new PlannedExercise("Barbell Row", 3, 10, 50, "Sırt"));
        return d;
    }

    private static WorkoutDayPlan restDay(String day) {
        return new WorkoutDayPlan(day, "REST", "Dinlenme & Mobilite", true);
    }
}
