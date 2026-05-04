package com.sporttracker.statisticsservice.service;

import com.sporttracker.statisticsservice.dto.StatisticCreateRequest;
import com.sporttracker.statisticsservice.model.Statistic;
import com.sporttracker.statisticsservice.repository.StatisticRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticRepository statisticRepository;

    public StatisticsServiceImpl(StatisticRepository statisticRepository) {
        this.statisticRepository = statisticRepository;
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
}
