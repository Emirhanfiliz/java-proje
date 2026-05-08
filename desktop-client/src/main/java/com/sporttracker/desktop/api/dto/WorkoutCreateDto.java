package com.sporttracker.desktop.api.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class WorkoutCreateDto {
    private String       userId;
    private String       name;
    private String       description;
    private String       date;
    private List<Object> exercises;

    public WorkoutCreateDto(String userId, String name, String description) {
        this.userId      = userId;
        this.name        = name;
        this.description = description;
        this.date        = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.exercises   = Collections.emptyList();
    }

    public String       getUserId()      { return userId; }
    public String       getName()        { return name; }
    public String       getDescription() { return description; }
    public String       getDate()        { return date; }
    public List<Object> getExercises()   { return exercises; }
}
