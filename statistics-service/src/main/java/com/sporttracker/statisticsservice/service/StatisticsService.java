package com.sporttracker.statisticsservice.service;

import com.sporttracker.statisticsservice.dto.StatisticCreateRequest;
import com.sporttracker.statisticsservice.model.Statistic;

import java.util.List;

public interface StatisticsService {

    Statistic record(StatisticCreateRequest request);

    List<Statistic> listByUserId(String userId);

    List<Statistic> listAll();

    Double calculateTotalDuration(String userId);

    double calculateAverageValueByType(String userId, String type);

    double calculateMaxValueByType(String userId, String type);

    long countByType(String userId, String type);
}
