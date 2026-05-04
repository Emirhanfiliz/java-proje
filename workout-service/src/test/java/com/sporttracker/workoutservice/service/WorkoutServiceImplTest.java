package com.sporttracker.workoutservice.service;

import com.sporttracker.shared.exception.CustomBusinessException;
import com.sporttracker.workoutservice.dto.WorkoutCreateRequest;
import com.sporttracker.workoutservice.model.Exercise;
import com.sporttracker.workoutservice.model.Workout;
import com.sporttracker.workoutservice.repository.WorkoutRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

        Workout result = workoutService.create(request);

        assertThat(result.getDate()).isEqualTo(fixedDate);
        verify(workoutRepository).save(any(Workout.class));
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

        Workout result = workoutService.create(request);

        assertThat(result.getDate()).isAfter(before);
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

        Workout result = workoutService.create(request);

        assertThat(result.getExercises()).isNotNull().isEmpty();
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

        Workout result = workoutService.create(request);

        assertThat(result.getExercises()).hasSize(2);
        assertThat(result.getExercises().get(0).getName()).isEqualTo("Push-up");
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
        when(workoutRepository.findByUserId("u1")).thenReturn(workouts);

        List<Workout> result = workoutService.listByUserId("u1");

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("listByUserId: kayıt yoksa boş liste dönmeli")
    void listByUserId_shouldReturnEmptyListWhenNoWorkouts() {
        when(workoutRepository.findByUserId("u2")).thenReturn(Collections.emptyList());

        List<Workout> result = workoutService.listByUserId("u2");

        assertThat(result).isEmpty();
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete: mevcut workout silinebilmeli")
    void delete_shouldDeleteWhenWorkoutExists() {
        when(workoutRepository.existsById("w1")).thenReturn(true);

        workoutService.delete("w1");

        verify(workoutRepository).deleteById("w1");
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
