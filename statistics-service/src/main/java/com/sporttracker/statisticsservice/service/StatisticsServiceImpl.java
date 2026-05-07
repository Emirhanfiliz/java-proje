package com.sporttracker.statisticsservice.service;

import com.sporttracker.shared.dto.WorkoutDto;
import com.sporttracker.shared.response.ApiResponse;
import com.sporttracker.statisticsservice.client.WorkoutClient;
import com.sporttracker.statisticsservice.dto.StatisticCreateRequest;
import com.sporttracker.statisticsservice.model.Statistic;
import com.sporttracker.statisticsservice.repository.StatisticRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticRepository statisticRepository;
    private final WorkoutClient workoutClient;

    public StatisticsServiceImpl(StatisticRepository statisticRepository, WorkoutClient workoutClient) {
        this.statisticRepository = statisticRepository;
        this.workoutClient = workoutClient;
    }

    @Override
    public Statistic record(StatisticCreateRequest request) {
        LocalDateTime when = request.getCalculationDate() != null
                ? request.getCalculationDate()
                : LocalDateTime.now();
        Statistic statistic = Statistic.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .value(request.getValue())
                .calculationDate(when)
                .build();
        return statisticRepository.save(statistic);
    }

    @Override
    public List<Statistic> listByUserId(String userId) {
        return statisticRepository.findByUserId(userId);
    }

    @Override
    public List<Statistic> listAll() {
        return statisticRepository.findAll();
    }

    @Override
    public Double calculateTotalDuration(String userId) {
        ApiResponse<List<WorkoutDto>> response = workoutClient.listWorkoutsByUser(userId);
        if (response.isSuccess() && response.getData() != null) {
            return response.getData().stream()
                    .mapToDouble(w -> w.getDurationInMinutes() != null ? w.getDurationInMinutes() : 0.0)
                    .sum();
        }
        return 0.0;
    }
}
