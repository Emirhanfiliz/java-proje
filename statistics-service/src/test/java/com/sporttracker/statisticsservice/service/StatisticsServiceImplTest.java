package com.sporttracker.statisticsservice.service;

import com.sporttracker.statisticsservice.dto.StatisticCreateRequest;
import com.sporttracker.statisticsservice.model.Statistic;
import com.sporttracker.statisticsservice.repository.StatisticRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceImplTest {

    @Mock
    private StatisticRepository statisticRepository;

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    // ── record ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("record: verilen tarih istatistiğe atanmalı")
    void record_shouldUseProvidedCalculationDate() {
        LocalDateTime fixedDate = LocalDateTime.of(2024, 5, 20, 12, 0);
        StatisticCreateRequest request = StatisticCreateRequest.builder()
                .userId("u1")
                .type("CALORIE_BURNED")
                .value(350.0)
                .calculationDate(fixedDate)
                .build();

        Statistic saved = Statistic.builder()
                .id(1L)
                .userId("u1")
                .type("CALORIE_BURNED")
                .value(350.0)
                .calculationDate(fixedDate)
                .build();

        when(statisticRepository.save(any(Statistic.class))).thenReturn(saved);

        Statistic result = statisticsService.record(request);

        assertThat(result.getCalculationDate()).isEqualTo(fixedDate);
        assertThat(result.getValue()).isEqualTo(350.0);
        verify(statisticRepository).save(any(Statistic.class));
    }

    @Test
    @DisplayName("record: tarih null gelirse LocalDateTime.now() kullanılmalı")
    void record_shouldFallbackToNowWhenDateIsNull() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        StatisticCreateRequest request = StatisticCreateRequest.builder()
                .userId("u1")
                .type("WORKOUT_DURATION")
                .value(45.0)
                .calculationDate(null)
                .build();

        when(statisticRepository.save(any(Statistic.class))).thenAnswer(inv -> inv.getArgument(0));

        Statistic result = statisticsService.record(request);

        assertThat(result.getCalculationDate()).isAfter(before);
    }

    @Test
    @DisplayName("record: userId ve type doğru şekilde kaydedilmeli")
    void record_shouldMapUserIdAndType() {
        StatisticCreateRequest request = StatisticCreateRequest.builder()
                .userId("u42")
                .type("STEPS_COUNT")
                .value(8000.0)
                .build();

        when(statisticRepository.save(any(Statistic.class))).thenAnswer(inv -> inv.getArgument(0));

        Statistic result = statisticsService.record(request);

        assertThat(result.getUserId()).isEqualTo("u42");
        assertThat(result.getType()).isEqualTo("STEPS_COUNT");
    }

    // ── listByUserId ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("listByUserId: kullanıcıya ait istatistikler dönmeli")
    void listByUserId_shouldReturnUserStatistics() {
        List<Statistic> stats = List.of(
                Statistic.builder().id(1L).userId("u1").type("CALORIE_BURNED").value(200.0).build(),
                Statistic.builder().id(2L).userId("u1").type("WORKOUT_DURATION").value(30.0).build()
        );
        when(statisticRepository.findByUserId("u1")).thenReturn(stats);

        List<Statistic> result = statisticsService.listByUserId("u1");

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(s -> "u1".equals(s.getUserId()));
    }

    @Test
    @DisplayName("listByUserId: kayıt yoksa boş liste dönmeli")
    void listByUserId_shouldReturnEmptyListWhenNoStatistics() {
        when(statisticRepository.findByUserId("u99")).thenReturn(Collections.emptyList());

        List<Statistic> result = statisticsService.listByUserId("u99");

        assertThat(result).isEmpty();
    }

    // ── listAll ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("listAll: sistemdeki tüm istatistikler dönmeli")
    void listAll_shouldReturnAllStatistics() {
        List<Statistic> allStats = List.of(
                Statistic.builder().id(1L).userId("u1").type("CALORIE_BURNED").value(100.0).build(),
                Statistic.builder().id(2L).userId("u2").type("STEPS_COUNT").value(5000.0).build(),
                Statistic.builder().id(3L).userId("u3").type("WORKOUT_DURATION").value(60.0).build()
        );
        when(statisticRepository.findAll()).thenReturn(allStats);

        List<Statistic> result = statisticsService.listAll();

        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("listAll: hiç istatistik yoksa boş liste dönmeli")
    void listAll_shouldReturnEmptyListWhenNoData() {
        when(statisticRepository.findAll()).thenReturn(Collections.emptyList());

        List<Statistic> result = statisticsService.listAll();

        assertThat(result).isEmpty();
    }
}
