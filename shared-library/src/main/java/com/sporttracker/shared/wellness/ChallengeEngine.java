package com.sporttracker.shared.wellness;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Generates weekly challenges and awards badges as challenges are completed.
 */
public final class ChallengeEngine {

    private ChallengeEngine() {}

    public static void ensureWeeklyChallenges(WellnessData data) {
        if (data == null) return;
        LocalDate weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        boolean alreadyHasCurrent = data.getChallenges().stream()
                .anyMatch(c -> weekStart.toString().equals(c.getStartDate()));
        if (alreadyHasCurrent) return;

        List<Challenge> challenges = new ArrayList<>(Arrays.asList(
                new Challenge(UUID.randomUUID().toString(), "4 Antrenmanı Tamamla", "Hafta içinde 4 antrenman tamamla",
                        "🏆", 4, "antrenman", weekStart.toString(), weekEnd.toString()),
                new Challenge(UUID.randomUUID().toString(), "70.000 Adım", "Hafta boyunca toplam 70.000 adım at",
                        "🚶", 70000, "adım", weekStart.toString(), weekEnd.toString()),
                new Challenge(UUID.randomUUID().toString(), "Su Şampiyonu", "Her gün 8+ bardak su iç",
                        "💧", 7, "gün", weekStart.toString(), weekEnd.toString()),
                new Challenge(UUID.randomUUID().toString(), "Recovery Master", "Ortalama recovery skorunu 75 üzerine çıkar",
                        "💤", 75, "puan", weekStart.toString(), weekEnd.toString()),
                new Challenge(UUID.randomUUID().toString(), "Yeni PR Kır", "Hafta içinde en az 1 PR güncelle",
                        "💪", 1, "PR", weekStart.toString(), weekEnd.toString())
        ));

        data.getChallenges().addAll(challenges);
    }

    public static int incrementProgress(WellnessData data, String challengeId, int amount) {
        if (data == null) return 0;
        for (Challenge c : data.getChallenges()) {
            if (c.getId().equals(challengeId) && Challenge.STATUS_ACTIVE.equals(c.getStatus())) {
                int newValue = Math.max(0, c.getCurrentValue() + amount);
                c.setCurrentValue(Math.min(newValue, c.getTargetValue()));
                if (c.getCurrentValue() >= c.getTargetValue()) {
                    c.setStatus(Challenge.STATUS_COMPLETED);
                    String badge = c.getBadgeIcon() + " " + c.getTitle();
                    if (!data.getEarnedBadges().contains(badge)) {
                        data.getEarnedBadges().add(badge);
                    }
                }
                return c.getCurrentValue();
            }
        }
        return 0;
    }

    public static void evaluate(WellnessData data, int weeklyWorkouts, int weeklySteps, int waterStreakDays,
                                double averageRecovery, int weeklyPrCount) {
        if (data == null) return;
        LocalDate weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        for (Challenge c : data.getChallenges()) {
            if (!weekStart.toString().equals(c.getStartDate())) continue;
            if (!Challenge.STATUS_ACTIVE.equals(c.getStatus())) continue;
            int progress = 0;
            switch (c.getTitle()) {
                case "4 Antrenmanı Tamamla":
                    progress = weeklyWorkouts;
                    break;
                case "70.000 Adım":
                    progress = weeklySteps;
                    break;
                case "Su Şampiyonu":
                    progress = waterStreakDays;
                    break;
                case "Recovery Master":
                    progress = (int) Math.round(averageRecovery);
                    break;
                case "Yeni PR Kır":
                    progress = weeklyPrCount;
                    break;
                default:
                    progress = c.getCurrentValue();
            }
            c.setCurrentValue(Math.min(c.getTargetValue(), Math.max(0, progress)));
            if (c.getCurrentValue() >= c.getTargetValue()) {
                c.setStatus(Challenge.STATUS_COMPLETED);
                String badge = c.getBadgeIcon() + " " + c.getTitle();
                if (!data.getEarnedBadges().contains(badge)) {
                    data.getEarnedBadges().add(badge);
                }
            }
        }
    }
}
