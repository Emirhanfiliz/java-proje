package com.sporttracker.workoutservice.controller;

import com.sporttracker.shared.response.ApiResponse;
import com.sporttracker.workoutservice.dto.WorkoutCreateRequest;
import com.sporttracker.workoutservice.model.Workout;
import com.sporttracker.workoutservice.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Workout>> listByUser(@PathVariable String userId) {
        return ApiResponse.success(workoutService.listByUserId(userId));
    }

    @GetMapping("/{id}")
    public ApiResponse<Workout> getById(@PathVariable String id) {
        return ApiResponse.success(workoutService.getById(id));
    }

    @PostMapping
    public ApiResponse<Workout> create(@Valid @RequestBody WorkoutCreateRequest request) {
        return ApiResponse.success(workoutService.create(request), "Workout created");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        workoutService.delete(id);
        return ApiResponse.success(null, "Workout deleted");
    }
}
