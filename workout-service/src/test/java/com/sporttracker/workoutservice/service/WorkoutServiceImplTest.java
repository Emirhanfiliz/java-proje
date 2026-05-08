package com.sporttracker.workoutservice.service;

import com.sporttracker.shared.exception.CustomBusinessException;
import com.sporttracker.workoutservice.dto.WorkoutCreateRequest;
import com.sporttracker.workoutservice.model.Exercise;
import com.sporttracker.workoutservice.model.Workout;
import com.sporttracker.workoutservice.repository.WorkoutRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceImplTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @InjectMocks
    private WorkoutServiceImpl workoutService;

    @Captor
    private ArgumentCaptor<Workout> workoutCaptor;

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("create: verilen tarih workout'a atanmalı")
    void create_shouldUseProvidedDate() {
        LocalDateTime fixedDate = LocalDateTime.of(2024, 6, 1, 9, 0);
        WorkoutCreateRequest request = WorkoutCreateRequest.builder()
                .userId("u1")
                .name("Morning Run")
                .date(fixedDate)
                .build();

        when(workoutRepository.save(any(Workout.class))).thenAnswer(inv -> inv.getArgument(0));

        workoutService.create(request);

        verify(workoutRepository, times(1)).save(workoutCaptor.capture());
        Workout captured = workoutCaptor.getValue();
        assertThat(captured.getDate()).isEqualTo(fixedDate);
        assertThat(captured.getUserId()).isEqualTo("u1");
        assertThat(captured.getName()).isEqualTo("Morning Run");
    }

    @Test
    @DisplayName("create: tarih null gelirse LocalDateTime.now() kullanılmalı")
    void create_shouldFallbackToNowWhenDateIsNull() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        WorkoutCreateRequest request = WorkoutCreateRequest.builder()
                .userId("u1")
                .name("Yoga")
                .date(null)
                .build();

        when(workoutRepository.save(any(Workout.class))).thenAnswer(inv -> inv.getArgument(0));

        workoutService.create(request);

        verify(workoutRepository).save(workoutCaptor.capture());
        assertThat(workoutCaptor.getValue().getDate()).isAfter(before);
    }

    @Test
    @DisplayName("create: exercises null gelirse boş liste atanmalı")
    void create_shouldUseEmptyListWhenExercisesIsNull() {
        WorkoutCreateRequest request = WorkoutCreateRequest.builder()
                .userId("u1")
                .name("Stretching")
                .exercises(null)
                .build();

        when(workoutRepository.save(any(Workout.class))).thenAnswer(inv -> inv.getArgument(0));

        workoutService.create(request);

        verify(workoutRepository).save(workoutCaptor.capture());
        assertThat(workoutCaptor.getValue().getExercises()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("create: verilen egzersiz listesi korunmalı")
    void create_shouldPreserveExercises() {
        List<Exercise> exercises = List.of(
                Exercise.builder().name("Push-up").sets(3).reps(10).build(),
                Exercise.builder().name("Squat").sets(4).reps(12).build()
        );
        WorkoutCreateRequest request = WorkoutCreateRequest.builder()
                .userId("u1")
                .name("Chest & Legs")
                .exercises(exercises)
                .build();

        when(workoutRepository.save(any(Workout.class))).thenAnswer(inv -> inv.getArgument(0));

        workoutService.create(request);

        verify(workoutRepository).save(workoutCaptor.capture());
        Workout captured = workoutCaptor.getValue();
        assertThat(captured.getExercises()).hasSize(2);
        assertThat(captured.getExercises().get(0).getName()).isEqualTo("Push-up");
        assertThat(captured.getExercises().get(1).getName()).isEqualTo("Squat");
    }

    @Test
    @DisplayName("create: durationInMinutes doğru aktarılmalı")
    void create_shouldMapDurationInMinutes() {
        WorkoutCreateRequest request = WorkoutCreateRequest.builder()
                .userId("u1")
                .name("HIIT")
                .durationInMinutes(30)
                .build();

        when(workoutRepository.save(any(Workout.class))).thenAnswer(inv -> inv.getArgument(0));

        workoutService.create(request);

        verify(workoutRepository).save(workoutCaptor.capture());
        assertThat(workoutCaptor.getValue().getDurationInMinutes()).isEqualTo(30);
    }

    // ── getById ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getById: mevcut id için workout dönmeli")
    void getById_shouldReturnWorkoutWhenFound() {
        Workout workout = Workout.builder().id("w1").userId("u1").name("Run").build();
        when(workoutRepository.findById("w1")).thenReturn(Optional.of(workout));

        Workout result = workoutService.getById("w1");

        assertThat(result.getId()).isEqualTo("w1");
        assertThat(result.getName()).isEqualTo("Run");
        verify(workoutRepository, times(1)).findById("w1");
    }

    @Test
    @DisplayName("getById: bulunamazsa CustomBusinessException fırlatmalı")
    void getById_shouldThrowWhenNotFound() {
        when(workoutRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutService.getById("missing"))
                .isInstanceOf(CustomBusinessException.class)
                .hasMessageContaining("Workout not found");
    }

    // ── listByUserId ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("listByUserId: kullanıcıya ait tüm workout'lar dönmeli")
    void listByUserId_shouldReturnAllWorkoutsForUser() {
        List<Workout> workouts = List.of(
                Workout.builder().id("w1").userId("u1").name("Run").build(),
                Workout.builder().id("w2").userId("u1").name("Swim").build()
        );
        when(workoutRepository.findByUserIdOrderByDateDesc("u1")).thenReturn(workouts);

        List<Workout> result = workoutService.listByUserId("u1");

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(w -> "u1".equals(w.getUserId()));
        verify(workoutRepository, times(1)).findByUserIdOrderByDateDesc("u1");
    }

    @Test
    @DisplayName("listByUserId: kayıt yoksa boş liste dönmeli")
    void listByUserId_shouldReturnEmptyListWhenNoWorkouts() {
        when(workoutRepository.findByUserIdOrderByDateDesc("u2")).thenReturn(Collections.emptyList());

        List<Workout> result = workoutService.listByUserId("u2");

        assertThat(result).isEmpty();
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete: mevcut workout silinebilmeli")
    void delete_shouldDeleteWhenWorkoutExists() {
        when(workoutRepository.existsById("w1")).thenReturn(true);

        workoutService.delete("w1");

        verify(workoutRepository, times(1)).deleteById("w1");
        verify(workoutRepository, times(1)).existsById("w1");
    }

    @Test
    @DisplayName("delete: olmayan workout için CustomBusinessException fırlatmalı")
    void delete_shouldThrowWhenWorkoutDoesNotExist() {
        when(workoutRepository.existsById("missing")).thenReturn(false);

        assertThatThrownBy(() -> workoutService.delete("missing"))
                .isInstanceOf(CustomBusinessException.class);

        verify(workoutRepository, never()).deleteById(any());
    }
}
