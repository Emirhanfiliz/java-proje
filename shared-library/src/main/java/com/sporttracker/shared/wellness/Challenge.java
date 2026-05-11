package com.sporttracker.shared.wellness;

import java.io.Serializable;

public class Challenge implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";

    private String id;
    private String title;
    private String description;
    private String badgeIcon;
    private int targetValue;
    private int currentValue;
    private String unit;
    private String status = STATUS_ACTIVE;
    private String startDate;
    private String endDate;

    public Challenge() {}

    public Challenge(String id, String title, String description, String badgeIcon,
                     int targetValue, String unit, String startDate, String endDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.badgeIcon = badgeIcon;
        this.targetValue = targetValue;
        this.unit = unit;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBadgeIcon() { return badgeIcon; }
    public void setBadgeIcon(String badgeIcon) { this.badgeIcon = badgeIcon; }

    public int getTargetValue() { return targetValue; }
    public void setTargetValue(int targetValue) { this.targetValue = targetValue; }

    public int getCurrentValue() { return currentValue; }
    public void setCurrentValue(int currentValue) { this.currentValue = currentValue; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public double progressRatio() {
        if (targetValue <= 0) return 0;
        return Math.min(1.0, currentValue / (double) targetValue);
    }

    public boolean isCompleted() {
        return STATUS_COMPLETED.equals(status) || currentValue >= targetValue;
    }
}
