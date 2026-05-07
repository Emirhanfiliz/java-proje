package com.sporttracker.workoutservice.repository;

import com.sporttracker.shared.repository.BaseRepository;
import com.sporttracker.workoutservice.model.Workout;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkoutRepository extends MongoRepository<Workout, String>, BaseRepository<Workout, String> {

    List<Workout> findByUserIdOrderByDateDesc(String userId);

    List<Workout> findByUserIdAndDateBetweenOrderByDateDesc(String userId, LocalDateTime start, LocalDateTime end);
}
