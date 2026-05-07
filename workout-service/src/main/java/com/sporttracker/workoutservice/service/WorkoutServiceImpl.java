package com.sporttracker.workoutservice.service;

import com.sporttracker.shared.exception.CustomBusinessException;
import com.sporttracker.workoutservice.dto.WorkoutCreateRequest;
import com.sporttracker.workoutservice.model.Exercise;
import com.sporttracker.workoutservice.model.Workout;
import com.sporttracker.workoutservice.repository.WorkoutRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutRepository workoutRepository;

    public WorkoutServiceImpl(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    @Override
    public List<Workout> listByUserId(String userId) {
        return workoutRepository.findByUserIdOrderByDateDesc(userId);
    }

    @Override
    public List<Workout> listByUserIdAndDateRange(String userId, LocalDateTime start, LocalDateTime end) {
        return workoutRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, start, end);
    }

    @Override
    public Workout getById(String id) {
        return workoutRepository.findById(id)
                .orElseThrow(() -> new CustomBusinessException("Workout not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public Workout create(WorkoutCreateRequest request) {
        LocalDateTime when = request.getDate() != null ? request.getDate() : LocalDateTime.now();
        List<Exercise> exercises = request.getExercises() != null
                ? request.getExercises()
                : Collections.emptyList();
        Workout workout = Workout.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .description(request.getDescription())
                .date(when)
                .durationInMinutes(request.getDurationInMinutes())
                .exercises(exercises)
                .build();
        return workoutRepository.save(workout);
    }

    @Override
    public void delete(String id) {
        if (!workoutRepository.existsById(id)) {
            throw new CustomBusinessException("Workout not found", HttpStatus.NOT_FOUND);
        }
        workoutRepository.deleteById(id);
    }
}
