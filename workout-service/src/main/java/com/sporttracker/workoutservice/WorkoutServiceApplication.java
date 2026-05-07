package com.sporttracker.workoutservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication(scanBasePackages = "com.sporttracker")
@EnableMongoAuditing
public class WorkoutServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkoutServiceApplication.class, args);
    }
}
