package com.sporttracker.shared.dto;

import java.time.LocalDateTime;
import java.util.List;

public class WorkoutDto {
    private String          id;
    private String          userId;
    private String          name;
    private String          description;
    private LocalDateTime   date;
    private Integer         durationInMinutes;
    private List<ExerciseDto> exercises;

    public WorkoutDto() {}

    public String          getId()               { return id; }
    public String          getUserId()           { return userId; }
    public String          getName()             { return name; }
    public String          getDescription()      { return description; }
    public LocalDateTime   getDate()             { return date; }
    public Integer         getDurationInMinutes(){ return durationInMinutes; }
    public List<ExerciseDto> getExercises()      { return exercises; }

    public void setId(String id)                         { this.id = id; }
    public void setUserId(String userId)                 { this.userId = userId; }
    public void setName(String name)                     { this.name = name; }
    public void setDescription(String description)       { this.description = description; }
    public void setDate(LocalDateTime date)              { this.date = date; }
    public void setDurationInMinutes(Integer d)          { this.durationInMinutes = d; }
    public void setExercises(List<ExerciseDto> exercises){ this.exercises = exercises; }
}
