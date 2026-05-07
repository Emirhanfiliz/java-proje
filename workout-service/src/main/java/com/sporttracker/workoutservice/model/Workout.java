package com.sporttracker.workoutservice.model;

import com.sporttracker.shared.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "workouts")
public class Workout implements BaseEntity<String> {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String name;
    private String description;
    private LocalDateTime date;
    private Integer durationInMinutes;

    @Builder.Default
    private List<Exercise> exercises = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @Override
    public String getId() { return id; }

    @Override
    public void setId(String id) { this.id = id; }
}
