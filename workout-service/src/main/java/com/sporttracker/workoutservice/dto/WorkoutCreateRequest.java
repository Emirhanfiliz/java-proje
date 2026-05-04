package com.sporttracker.workoutservice.dto;

import com.sporttracker.workoutservice.model.Exercise;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "userId is required")
    private String userId;

    @NotBlank(message = "name is required")
    private String name;

    private String description;
    private LocalDateTime date;
    private List<Exercise> exercises;
}
