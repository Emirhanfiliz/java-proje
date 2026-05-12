package com.sporttracker.desktop.api.dto;

import java.util.Collections;
import java.util.List;

public class WorkoutDto {
    private String                id;
    private String                userId;
    private String                name;
    private String                description;
    private String                date;
    private Integer               durationInMinutes;
    private List<ExerciseItemDto> exercises;

    public String                getId()                { return id; }
    public String                getUserId()            { return userId; }
    public String                getName()              { return name; }
    public String                getDescription()       { return description; }
    public String                getDate()              { return date; }
    public Integer               getDurationInMinutes() { return durationInMinutes != null ? durationInMinutes : 0; }
    public List<ExerciseItemDto> getExercises()         { return exercises != null ? exercises : Collections.emptyList(); }

    public String toDisplayString() {
        String dateShort = (date != null && date.length() >= 10) ? date.substring(0, 10) : "";
        String dur = durationInMinutes != null && durationInMinutes > 0 ? "  •  " + durationInMinutes + " dk" : "";
        String exCount = exercises != null && !exercises.isEmpty() ? "  •  " + exercises.size() + " egz." : "";
        return name + dur + exCount + (dateShort.isEmpty() ? "" : "  •  " + dateShort);
    }
}
