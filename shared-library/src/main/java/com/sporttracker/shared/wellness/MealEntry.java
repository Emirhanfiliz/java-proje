package com.sporttracker.shared.wellness;

import java.io.Serializable;

public class MealEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String SLOT_BREAKFAST = "BREAKFAST";
    public static final String SLOT_LUNCH = "LUNCH";
    public static final String SLOT_DINNER = "DINNER";
    public static final String SLOT_SNACK = "SNACK";

    private String id;
    private String date;
    private String slot;
    private String name;
    private int calories;
    private double proteinGrams;
    private double carbsGrams;
    private double fatGrams;
    private String note;

    public MealEntry() {}

    public MealEntry(String id, String date, String slot, String name,
                     int calories, double proteinGrams, double carbsGrams, double fatGrams) {
        this.id = id;
        this.date = date;
        this.slot = slot;
        this.name = name;
        this.calories = calories;
        this.proteinGrams = proteinGrams;
        this.carbsGrams = carbsGrams;
        this.fatGrams = fatGrams;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public double getProteinGrams() { return proteinGrams; }
    public void setProteinGrams(double proteinGrams) { this.proteinGrams = proteinGrams; }

    public double getCarbsGrams() { return carbsGrams; }
    public void setCarbsGrams(double carbsGrams) { this.carbsGrams = carbsGrams; }

    public double getFatGrams() { return fatGrams; }
    public void setFatGrams(double fatGrams) { this.fatGrams = fatGrams; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
