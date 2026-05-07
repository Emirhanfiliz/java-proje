package com.sporttracker.statisticsservice.client;

import com.sporttracker.shared.dto.WorkoutDto;
import com.sporttracker.shared.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "workout-service", url = "${workout-service.url:http://localhost:8082}")
public interface WorkoutClient {

    @GetMapping("/api/v1/workouts/user/{userId}")
    ApiResponse<List<WorkoutDto>> listWorkoutsByUser(@PathVariable("userId") String userId);
}
