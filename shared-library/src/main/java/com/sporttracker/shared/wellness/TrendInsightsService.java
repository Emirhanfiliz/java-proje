package com.sporttracker.shared.wellness;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/**
 * Calculates 7 / 30 / 90 day trend insights from local wellness data.
 */
public final class TrendInsightsService {

    public static final int PERIOD_7 = 7;
    public static final int PERIOD_30 = 30;
    public static final int PERIOD_90 = 90;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    private TrendInsightsService() {}

    public static TrendInsight compute(WellnessData data, int days) {
        TrendInsight insight = new TrendInsight();
        insight.setPeriod(days + " gün");

        LocalDate today = LocalDate.now();
        LocalDate cutoff = today.minusDays(days);
        LocalDate previousCutoff = cutoff.minusDays(days);

        int totalLoad = 0;
        int totalLoadPrevious = 0;
        int recoverySum = 0;
        int recoveryCount = 0;
        int sessionCount = 0;
        int previousSessionCount = 0;
        int minutesTotal = 0;
        int caloriesTotal = 0;

        List<RecoveryEntry> recovery = data.getRecoveryHistory();
        for (RecoveryEntry entry : recovery) {
            LocalDate date = parseDate(entry.getDate());
            if (date == null) continue;
            if (!date.isBefore(cutoff) && !date.isAfter(today)) {
                totalLoad += entry.getTrainingLoad();
                recoverySum += entry.getScore();
                recoveryCount++;
                sessionCount++;
                minutesTotal += entry.getTrainingLoad() / 5;
                caloriesTotal += entry.getTrainingLoad() * 4;
            } else if (!date.isBefore(previousCutoff) && date.isBefore(cutoff)) {
                totalLoadPrevious += entry.getTrainingLoad();
                previousSessionCount++;
            }
        }

        List<HrZoneSession> hrSessions = data.getHrZoneSessions();
        for (HrZoneSession session : hrSessions) {
            LocalDate date = parseDate(session.getDate());
            if (date == null) continue;
            if (!date.isBefore(cutoff) && !date.isAfter(today)) {
                sessionCount++;
                minutesTotal += session.getDurationMinutes();
                caloriesTotal += session.getDurationMinutes() * 7;
            } else if (!date.isBefore(previousCutoff) && date.isBefore(cutoff)) {
                previousSessionCount++;
            }
        }

        double trend = 0;
        if (totalLoadPrevious > 0) {
            trend = ((totalLoad - totalLoadPrevious) / (double) totalLoadPrevious) * 100.0;
        } else if (totalLoad > 0) {
            trend = 100.0;
        }

        double avgRecovery = recoveryCount == 0 ? 0 : recoverySum / (double) recoveryCount;

        insight.setTotalWorkouts(sessionCount);
        insight.setTotalMinutes(minutesTotal);
        insight.setTotalCalories(caloriesTotal);
        insight.setAverageRecoveryScore(round1(avgRecovery));
        insight.setTrendPercentage(round1(trend));
        insight.setAdvice(adviceFor(trend, avgRecovery, sessionCount, previousSessionCount, days));
        insight.setMotivation(motivationFor(trend, avgRecovery));
        return insight;
    }

    private static String adviceFor(double trend, double avgRecovery, int sessions, int previousSessions, int days) {
        if (sessions == 0) {
            return "Bu " + days + " gün içinde antrenman verisi yok. Programa geri dönmek için bugün küçük bir oturum dene.";
        }
        if (trend >= 15) {
            if (avgRecovery < 60) {
                return "Performansın yükseliyor ama toparlanma skorun düşük. 1-2 günlük deload ile riski azalt.";
            }
            return String.format(Locale.US, "Performansın %.0f%% arttı, momentumu koru. Yeni bir kişisel rekor hedefle.", trend);
        }
        if (trend <= -15) {
            return "Antrenman yükünde düşüş var. Adaptive Goals önerisi: hedefleri kısa süre azalt, motivasyonu yeniden kur.";
        }
        if (avgRecovery < 55) {
            return "Performans stabil ama toparlanma düşük. Uyku 7+ saat, su 8+ bardak hedefiyle haftayı tamamla.";
        }
        return String.format(Locale.US, "Performansın stabil (±%.0f%%). Şimdi hacim yerine teknik kalitesine odaklan.", Math.abs(trend));
    }

    private static String motivationFor(double trend, double avgRecovery) {
        if (trend >= 20) return "Müthiş ivme! Şimdi disiplin zamanı.";
        if (trend >= 5) return "Yukarı doğru ilerliyorsun. Devam!";
        if (trend <= -20) return "Hızlı bir yeniden başlangıç sana iyi gelecek.";
        if (avgRecovery >= 75) return "Toparlanman muhteşem. Bunu kullanma zamanı.";
        return "Tutarlılık her şeyden değerli. Bugüne odaklan.";
    }

    private static double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private static LocalDate parseDate(String iso) {
        if (iso == null || iso.isBlank()) return null;
        try {
            return LocalDate.parse(iso.length() > 10 ? iso.substring(0, 10) : iso, DATE_FMT);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}
