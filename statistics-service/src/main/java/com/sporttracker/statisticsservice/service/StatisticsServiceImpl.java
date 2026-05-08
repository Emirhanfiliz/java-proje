package com.sporttracker.statisticsservice.service;

import com.sporttracker.shared.dto.WorkoutDto;
import com.sporttracker.shared.response.ApiResponse;
import com.sporttracker.statisticsservice.client.WorkoutClient;
import com.sporttracker.statisticsservice.dto.StatisticCreateRequest;
import com.sporttracker.statisticsservice.model.Statistic;
import com.sporttracker.statisticsservice.repository.StatisticRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public double calculateAverageValueByType(String userId, String type) {
        return statisticRepository.findByUserId(userId).stream()
                .filter(s -> type.equals(s.getType()))
                .mapToDouble(Statistic::getValue)
                .average()
                .orElse(0.0);
    }

    @Override
    public double calculateMaxValueByType(String userId, String type) {
        return statisticRepository.findByUserId(userId).stream()
                .filter(s -> type.equals(s.getType()))
                .mapToDouble(Statistic::getValue)
                .max()
                .orElse(0.0);
    }

    @Override
    public long countByType(String userId, String type) {
        return statisticRepository.findByUserId(userId).stream()
                .filter(s -> type.equals(s.getType()))
                .count();
    }

    @Override
    @Transactional
    public Double calculateTotalDuration(String userId) {
        ApiResponse<List<WorkoutDto>> response = workoutClient.listWorkoutsByUser(userId);
        if (response.isSuccess() && response.getData() != null) {
            double totalDuration = response.getData().stream()
                    .mapToDouble(w -> w.getDurationInMinutes() != null ? w.getDurationInMinutes() : 0.0)
                    .sum();
                    
            // Veri tutarlılığı kontrolü: NoSQL'den gelen veriyi JDBC katmanına senkronize et (Upsert mantığı)
            Statistic stat = statisticRepository.findByUserId(userId).stream()
                    .filter(s -> "TOTAL_DURATION".equals(s.getType()))
                    .findFirst()
                    .orElse(Statistic.builder().userId(userId).type("TOTAL_DURATION").build());
                    
            stat.setValue(totalDuration);
            stat.setCalculationDate(LocalDateTime.now());
            statisticRepository.save(stat);
            
            return totalDuration;
        }
        return 0.0;
    }
}
