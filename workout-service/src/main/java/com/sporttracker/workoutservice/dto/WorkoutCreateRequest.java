package com.sporttracker.workoutservice.dto;

import com.sporttracker.workoutservice.model.Exercise;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutCreateRequest {

    @NotBlank
    private String userId;

    @NotBlank
    private String name;

    private String description;
    
    private LocalDateTime date;

    @NotNull
    private Integer durationInMinutes;

    private List<Exercise> exercises;
}
