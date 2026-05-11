package com.sporttracker.shared.wellness;

import java.io.Serializable;

public class Reminder implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String TYPE_WORKOUT = "WORKOUT";
    public static final String TYPE_WATER = "WATER";
    public static final String TYPE_SLEEP = "SLEEP";
    public static final String TYPE_STRETCH = "STRETCH";
    public static final String TYPE_MEAL = "MEAL";
    public static final String TYPE_HABIT = "HABIT";

    private String id;
    private String label;
    private String message;
    private String type;
    private int hour;
    private int minute;
    private boolean enabled = true;

    public Reminder() {}

    public Reminder(String id, String label, String message, String type, int hour, int minute) {
        this.id = id;
        this.label = label;
        this.message = message;
        this.type = type;
        this.hour = hour;
        this.minute = minute;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getHour() { return hour; }
    public void setHour(int hour) { this.hour = hour; }

    public int getMinute() { return minute; }
    public void setMinute(int minute) { this.minute = minute; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String formattedTime() {
        return String.format("%02d:%02d", hour, minute);
    }
}
