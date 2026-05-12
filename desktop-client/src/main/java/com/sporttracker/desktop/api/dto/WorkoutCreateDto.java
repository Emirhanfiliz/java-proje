package com.sporttracker.desktop.api.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class WorkoutCreateDto {
    private String               userId;
    private String               name;
    private String               description;
    private String               date;
    private int                  durationInMinutes;
    private List<ExerciseItemDto> exercises;

    public WorkoutCreateDto(String userId, String name, String description,
                            int durationInMinutes, List<ExerciseItemDto> exercises) {
        this.userId            = userId;
        this.name              = name;
        this.description       = description;
        this.date              = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.durationInMinutes = durationInMinutes;
        this.exercises         = exercises != null ? exercises : Collections.emptyList();
    }

    public String                getUserId()           { return userId; }
    public String                getName()             { return name; }
    public String                getDescription()      { return description; }
    public String                getDate()             { return date; }
    public int                   getDurationInMinutes(){ return durationInMinutes; }
    public List<ExerciseItemDto> getExercises()        { return exercises; }
}
