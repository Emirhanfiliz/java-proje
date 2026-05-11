package com.sporttracker.shared.wellness;

/**
 * Computes a 0-100 recovery score based on sleep, resting heart rate, and training load.
 */
public final class RecoveryScoreCalculator {

    private RecoveryScoreCalculator() {}

    public static RecoveryEntry compute(RecoveryEntry input) {
        if (input == null) return null;

        double sleepScore = sleepScore(input.getSleepHours());
        double hrScore = restingHeartRateScore(input.getRestingHeartRate());
        double loadScore = trainingLoadScore(input.getTrainingLoad());

        double weighted = sleepScore * 0.45 + hrScore * 0.35 + loadScore * 0.20;
        int finalScore = (int) Math.round(Math.max(0, Math.min(100, weighted)));
        input.setScore(finalScore);
        input.setLabel(labelFor(finalScore));
        return input;
    }

    public static int compute(double sleepHours, int restingHeartRate, int trainingLoad) {
        RecoveryEntry tmp = new RecoveryEntry(null, sleepHours, restingHeartRate, trainingLoad);
        compute(tmp);
        return tmp.getScore();
    }

    public static String labelFor(int score) {
        if (score >= 85) return "Mükemmel toparlanma";
        if (score >= 70) return "İyi toparlanma";
        if (score >= 55) return "Orta toparlanma";
        if (score >= 40) return "Düşük toparlanma";
        return "Dinlenmen şart";
    }

    private static double sleepScore(double hours) {
        if (hours <= 0) return 30;
        if (hours >= 8.5) return 100;
        if (hours >= 7) return 70 + (hours - 7) * 20;
        if (hours >= 5) return 40 + (hours - 5) * 15;
        return 20 + hours * 4;
    }

    private static double restingHeartRateScore(int rhr) {
        if (rhr <= 0) return 60;
        if (rhr <= 55) return 100;
        if (rhr <= 65) return 90 - (rhr - 55);
        if (rhr <= 75) return 75 - (rhr - 65) * 2;
        if (rhr <= 90) return 55 - (rhr - 75) * 2.5;
        return 20;
    }

    private static double trainingLoadScore(int load) {
        if (load <= 0) return 90;
        if (load <= 200) return 95 - load * 0.05;
        if (load <= 500) return 85 - (load - 200) * 0.1;
        if (load <= 900) return 55 - (load - 500) * 0.05;
        return 25;
    }
}
