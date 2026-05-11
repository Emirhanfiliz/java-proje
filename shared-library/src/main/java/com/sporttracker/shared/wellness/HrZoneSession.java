package com.sporttracker.shared.wellness;

import java.io.Serializable;

public class HrZoneSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private String date;
    private String workoutName;
    private int averageHeartRate;
    private int durationMinutes;
    private int zone1Minutes;
    private int zone2Minutes;
    private int zone3Minutes;
    private int zone4Minutes;
    private int zone5Minutes;

    public HrZoneSession() {}

    public HrZoneSession(String date, String workoutName, int averageHeartRate, int durationMinutes) {
        this.date = date;
        this.workoutName = workoutName;
        this.averageHeartRate = averageHeartRate;
        this.durationMinutes = durationMinutes;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getWorkoutName() { return workoutName; }
    public void setWorkoutName(String workoutName) { this.workoutName = workoutName; }

    public int getAverageHeartRate() { return averageHeartRate; }
    public void setAverageHeartRate(int averageHeartRate) { this.averageHeartRate = averageHeartRate; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public int getZone1Minutes() { return zone1Minutes; }
    public void setZone1Minutes(int zone1Minutes) { this.zone1Minutes = zone1Minutes; }

    public int getZone2Minutes() { return zone2Minutes; }
    public void setZone2Minutes(int zone2Minutes) { this.zone2Minutes = zone2Minutes; }

    public int getZone3Minutes() { return zone3Minutes; }
    public void setZone3Minutes(int zone3Minutes) { this.zone3Minutes = zone3Minutes; }

    public int getZone4Minutes() { return zone4Minutes; }
    public void setZone4Minutes(int zone4Minutes) { this.zone4Minutes = zone4Minutes; }

    public int getZone5Minutes() { return zone5Minutes; }
    public void setZone5Minutes(int zone5Minutes) { this.zone5Minutes = zone5Minutes; }
}
