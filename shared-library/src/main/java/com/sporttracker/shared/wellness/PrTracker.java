package com.sporttracker.shared.wellness;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores and queries personal records by lift.
 */
public final class PrTracker {

    private PrTracker() {}

    public static void addRecord(WellnessData data, PrRecord record) {
        if (data == null || record == null) return;
        if (record.getDate() == null || record.getDate().isBlank()) {
            record.setDate(LocalDate.now().toString());
        }
        data.getPersonalRecords().add(record);
        data.getPersonalRecords().sort(Comparator
                .comparing(PrRecord::getExerciseName, Comparator.nullsLast(String::compareTo))
                .thenComparing(PrRecord::getDate, Comparator.nullsLast(String::compareTo)));
    }

    public static PrRecord bestFor(WellnessData data, String exerciseName) {
        if (data == null || exerciseName == null) return null;
        PrRecord best = null;
        double bestOneRm = 0;
        for (PrRecord record : data.getPersonalRecords()) {
            if (!exerciseName.equalsIgnoreCase(record.getExerciseName())) continue;
            double oneRm = record.estimatedOneRepMax();
            if (oneRm > bestOneRm) {
                bestOneRm = oneRm;
                best = record;
            }
        }
        return best;
    }

    public static Map<String, PrRecord> bestByLift(WellnessData data) {
        Map<String, PrRecord> bests = new LinkedHashMap<>();
        if (data == null) return bests;
        for (PrRecord record : data.getPersonalRecords()) {
            PrRecord existing = bests.get(record.getExerciseName());
            if (existing == null || record.estimatedOneRepMax() > existing.estimatedOneRepMax()) {
                bests.put(record.getExerciseName(), record);
            }
        }
        return bests;
    }

    public static List<PrRecord> historyFor(WellnessData data, String exerciseName) {
        List<PrRecord> result = new java.util.ArrayList<>();
        if (data == null || exerciseName == null) return result;
        for (PrRecord record : data.getPersonalRecords()) {
            if (exerciseName.equalsIgnoreCase(record.getExerciseName())) {
                result.add(record);
            }
        }
        result.sort(Comparator.comparing(PrRecord::getDate, Comparator.nullsLast(String::compareTo)));
        return result;
    }
}
