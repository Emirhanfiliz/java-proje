package com.sporttracker.workoutservice.service;

import com.sporttracker.workoutservice.dto.WorkoutCreateRequest;
import com.sporttracker.workoutservice.model.Workout;
import org.bson.Document;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkoutService {

    List<Workout> listByUserId(String userId);

    List<Workout> listByUserIdAndDateRange(String userId, LocalDateTime start, LocalDateTime end);

    Workout getById(String id);

    Workout create(WorkoutCreateRequest request);

    void delete(String id);

    Double getTotalDurationByUser(String userId);

    List<Document> getWorkoutCountByMonth(String userId);

    List<Document> getTopExercisesByFrequency(String userId, int limit);
}
