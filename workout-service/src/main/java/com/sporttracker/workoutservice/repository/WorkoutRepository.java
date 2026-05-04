package com.sporttracker.workoutservice.repository;

import com.sporttracker.shared.repository.BaseRepository;
import com.sporttracker.workoutservice.model.Workout;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutRepository extends MongoRepository<Workout, String>, BaseRepository<Workout, String> {
    List<Workout> findByUserId(String userId);
}
