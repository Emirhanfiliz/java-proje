package com.sporttracker.statisticsservice.controller;

import com.sporttracker.shared.response.ApiResponse;
import com.sporttracker.statisticsservice.dto.StatisticCreateRequest;
import com.sporttracker.statisticsservice.model.Statistic;
import com.sporttracker.statisticsservice.service.StatisticsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticController {

    private final StatisticsService statisticsService;

    public StatisticController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @PostMapping
    public ApiResponse<Statistic> record(@Valid @RequestBody StatisticCreateRequest request) {
        return ApiResponse.success(statisticsService.record(request), "Statistic recorded");
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Statistic>> listByUser(@PathVariable String userId) {
        return ApiResponse.success(statisticsService.listByUserId(userId));
    }

    @GetMapping
    public ApiResponse<List<Statistic>> listAll() {
        return ApiResponse.success(statisticsService.listAll());
    }
}
