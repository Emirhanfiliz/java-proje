package com.sporttracker.shared.dto;

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
public class WorkoutDto {
    private String id;
    private String userId;
    private String name;
    private String description;
    private LocalDateTime date;
    private Integer durationInMinutes;
    private List<ExerciseDto> exercises;
}
