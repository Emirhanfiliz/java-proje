package com.sporttracker.shared.wellness;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Habit implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String icon;
    private String description;
    private int streak;
    private String lastCompletedDate;
    private List<String> completedDates = new ArrayList<>();

    public Habit() {}

    public Habit(String id, String name, String icon, String description) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.description = description;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }

    public String getLastCompletedDate() { return lastCompletedDate; }
    public void setLastCompletedDate(String lastCompletedDate) { this.lastCompletedDate = lastCompletedDate; }

    public List<String> getCompletedDates() {
        if (completedDates == null) completedDates = new ArrayList<>();
        return completedDates;
    }

    public void setCompletedDates(List<String> completedDates) {
        this.completedDates = completedDates != null ? completedDates : new ArrayList<>();
    }
}
