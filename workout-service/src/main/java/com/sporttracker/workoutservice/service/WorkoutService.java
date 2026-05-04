package com.sporttracker.workoutservice.service;

import com.sporttracker.workoutservice.dto.WorkoutCreateRequest;
import com.sporttracker.workoutservice.model.Workout;

import java.util.List;

public interface WorkoutService {

    List<Workout> listByUserId(String userId);

    Workout getById(String id);

    Workout create(WorkoutCreateRequest request);

    void delete(String id);
}
