package com.sporttracker.workoutservice.service;

import com.sporttracker.shared.exception.CustomBusinessException;
import com.sporttracker.workoutservice.dto.WorkoutCreateRequest;
import com.sporttracker.workoutservice.model.Exercise;
import com.sporttracker.workoutservice.model.Workout;
import com.sporttracker.workoutservice.repository.WorkoutAggregationRepository;
import com.sporttracker.workoutservice.repository.WorkoutRepository;
import org.bson.Document;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final WorkoutAggregationRepository aggregationRepository;

    public WorkoutServiceImpl(WorkoutRepository workoutRepository,
                              WorkoutAggregationRepository aggregationRepository) {
        this.workoutRepository = workoutRepository;
        this.aggregationRepository = aggregationRepository;
    }

    @Override
    @Cacheable(value = "workouts", key = "#userId")
    public List<Workout> listByUserId(String userId) {
        return workoutRepository.findByUserIdOrderByDateDesc(userId);
    }

    @Override
    @Cacheable(value = "workouts-range", key = "#userId + '_' + #start + '_' + #end")
    public List<Workout> listByUserIdAndDateRange(String userId, LocalDateTime start, LocalDateTime end) {
        return workoutRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, start, end);
    }

    @Override
    public Workout getById(String id) {
        return workoutRepository.findById(id)
                .orElseThrow(() -> new CustomBusinessException("Workout not found", HttpStatus.NOT_FOUND));
    }

    @Override
    @CacheEvict(value = {"workouts", "workouts-range"}, key = "#request.userId")
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
    @CacheEvict(value = {"workouts", "workouts-range"}, allEntries = true)
    public void delete(String id) {
        if (!workoutRepository.existsById(id)) {
            throw new CustomBusinessException("Workout not found", HttpStatus.NOT_FOUND);
        }
        workoutRepository.deleteById(id);
    }

    @Override
    public Double getTotalDurationByUser(String userId) {
        return aggregationRepository.getTotalDurationByUser(userId);
    }

    @Override
    public List<Document> getWorkoutCountByMonth(String userId) {
        return aggregationRepository.getWorkoutCountByMonth(userId);
    }

    @Override
    public List<Document> getTopExercisesByFrequency(String userId, int limit) {
        return aggregationRepository.getTopExercisesByFrequency(userId, limit);
    }
}
