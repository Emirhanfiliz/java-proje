package com.sporttracker.shared.wellness;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Heart-rate zone analysis based on maximum heart rate (220 - age formula).
 */
public final class HeartRateZoneCalculator {

    private HeartRateZoneCalculator() {}

    public static int maxHeartRate(int ageYears) {
        return 220 - Math.max(10, Math.min(100, ageYears));
    }

    /**
     * Returns a zone number 1..5 for a given heart rate and age.
     */
    public static int zoneForHeartRate(int heartRate, int ageYears) {
        int max = maxHeartRate(ageYears);
        double ratio = heartRate / (double) max;
        if (ratio < 0.60) return 1;
        if (ratio < 0.70) return 2;
        if (ratio < 0.80) return 3;
        if (ratio < 0.90) return 4;
        return 5;
    }

    public static String zoneLabel(int zone) {
        switch (zone) {
            case 1: return "Zone 1 - Recovery";
            case 2: return "Zone 2 - Aerobik";
            case 3: return "Zone 3 - Tempo";
            case 4: return "Zone 4 - Eşik";
            case 5: return "Zone 5 - Maksimum";
            default: return "Zone " + zone;
        }
    }

    public static String zoneColor(int zone) {
        switch (zone) {
            case 1: return "#5ca9ff";
            case 2: return "#2ecc71";
            case 3: return "#f1c40f";
            case 4: return "#ff7f50";
            case 5: return "#ff416c";
            default: return "#888888";
        }
    }

    /**
     * Distributes total duration across zones using a heuristic based on
     * the average heart rate position relative to max heart rate.
     */
    public static HrZoneSession distribute(HrZoneSession session, int ageYears) {
        if (session == null) return null;
        int duration = Math.max(0, session.getDurationMinutes());
        int avg = Math.max(50, session.getAverageHeartRate());
        int max = maxHeartRate(ageYears);
        double ratio = avg / (double) max;

        double[] weights = weightsFor(ratio);
        int[] minutes = new int[5];
        int allocated = 0;
        for (int i = 0; i < 4; i++) {
            minutes[i] = (int) Math.round(duration * weights[i]);
            allocated += minutes[i];
        }
        minutes[4] = Math.max(0, duration - allocated);

        session.setZone1Minutes(minutes[0]);
        session.setZone2Minutes(minutes[1]);
        session.setZone3Minutes(minutes[2]);
        session.setZone4Minutes(minutes[3]);
        session.setZone5Minutes(minutes[4]);
        return session;
    }

    private static double[] weightsFor(double ratio) {
        if (ratio < 0.55) return new double[]{0.70, 0.20, 0.07, 0.02, 0.01};
        if (ratio < 0.65) return new double[]{0.30, 0.45, 0.15, 0.07, 0.03};
        if (ratio < 0.75) return new double[]{0.15, 0.30, 0.35, 0.15, 0.05};
        if (ratio < 0.85) return new double[]{0.10, 0.20, 0.30, 0.30, 0.10};
        return new double[]{0.05, 0.10, 0.20, 0.35, 0.30};
    }

    public static Map<String, Integer> summary(HrZoneSession session) {
        Map<String, Integer> summary = new LinkedHashMap<>();
        summary.put(zoneLabel(1), session.getZone1Minutes());
        summary.put(zoneLabel(2), session.getZone2Minutes());
        summary.put(zoneLabel(3), session.getZone3Minutes());
        summary.put(zoneLabel(4), session.getZone4Minutes());
        summary.put(zoneLabel(5), session.getZone5Minutes());
        return summary;
    }
}
