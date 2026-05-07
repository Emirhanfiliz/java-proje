package com.sporttracker.workoutservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {

    private String name;
    private Integer sets;
    private Integer reps;
    private Double weight;
    private Integer durationInSeconds;
    private String notes;
}
