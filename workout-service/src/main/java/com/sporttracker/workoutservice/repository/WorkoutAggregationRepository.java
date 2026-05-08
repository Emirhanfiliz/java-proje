package com.sporttracker.workoutservice.repository;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WorkoutAggregationRepository {

    private final MongoTemplate mongoTemplate;

    public WorkoutAggregationRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Double getTotalDurationByUser(String userId) {
        MatchOperation match = Aggregation.match(Criteria.where("userId").is(userId));
        GroupOperation group = Aggregation.group("userId")
                .sum("durationInMinutes").as("totalDuration");

        Aggregation aggregation = Aggregation.newAggregation(match, group);
        AggregationResults<Document> results =
                mongoTemplate.aggregate(aggregation, "workouts", Document.class);

        Document result = results.getUniqueMappedResult();
        if (result == null) return 0.0;
        Object val = result.get("totalDuration");
        return val instanceof Number ? ((Number) val).doubleValue() : 0.0;
    }

    public List<Document> getWorkoutCountByMonth(String userId) {
        MatchOperation match = Aggregation.match(Criteria.where("userId").is(userId));
        ProjectionOperation project = Aggregation.project()
                .andExpression("year(date)").as("year")
                .andExpression("month(date)").as("month");
        GroupOperation group = Aggregation.group("year", "month")
                .count().as("workoutCount");
        SortOperation sort = Aggregation.sort(Sort.by(
                Sort.Order.desc("_id.year"),
                Sort.Order.desc("_id.month")));

        Aggregation aggregation = Aggregation.newAggregation(match, project, group, sort);
        return mongoTemplate.aggregate(aggregation, "workouts", Document.class).getMappedResults();
    }

    public List<Document> getTopExercisesByFrequency(String userId, int limit) {
        MatchOperation match = Aggregation.match(Criteria.where("userId").is(userId));
        UnwindOperation unwind = Aggregation.unwind("exercises");
        GroupOperation group = Aggregation.group("exercises.name")
                .count().as("frequency");
        SortOperation sort = Aggregation.sort(Sort.by(Sort.Order.desc("frequency")));
        LimitOperation limitOp = Aggregation.limit(limit);

        Aggregation aggregation = Aggregation.newAggregation(match, unwind, group, sort, limitOp);
        return mongoTemplate.aggregate(aggregation, "workouts", Document.class).getMappedResults();
    }
}
