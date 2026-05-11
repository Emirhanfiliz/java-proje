package com.sporttracker.shared.wellness;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manages habit stacks (water, sleep, steps, stretching) and tracks streaks.
 */
public final class HabitEngine {

    private HabitEngine() {}

    public static List<Habit> defaultStack() {
        return new ArrayList<>(Arrays.asList(
                new Habit("habit-water", "Su İç", "💧", "Bugün en az 8 bardak su iç"),
                new Habit("habit-sleep", "Erken Uyu", "💤", "23:00'tan önce yatağa gir"),
                new Habit("habit-steps", "10K Adım", "🚶", "Günde 10.000 adım at"),
                new Habit("habit-stretch", "Esneme", "🧘", "5 dakika sabah esnemesi yap"),
                new Habit("habit-protein", "Protein Hedefi", "🥩", "Günlük protein hedefini tamamla")
        ));
    }

    public static void ensureDefaults(WellnessData data) {
        if (data == null) return;
        if (data.getHabits().isEmpty()) {
            data.setHabits(defaultStack());
        }
    }

    public static void markCompleted(Habit habit, LocalDate today) {
        if (habit == null) return;
        String iso = today.toString();
        if (habit.getCompletedDates().contains(iso)) return;
        habit.getCompletedDates().add(iso);
        if (habit.getCompletedDates().size() > 365) {
            habit.getCompletedDates().remove(0);
        }
        habit.setLastCompletedDate(iso);
        habit.setStreak(computeStreak(habit, today));
    }

    public static void resetCompletion(Habit habit, LocalDate today) {
        if (habit == null) return;
        String iso = today.toString();
        habit.getCompletedDates().remove(iso);
        if (iso.equals(habit.getLastCompletedDate())) {
            habit.setLastCompletedDate(habit.getCompletedDates().isEmpty()
                    ? null
                    : habit.getCompletedDates().get(habit.getCompletedDates().size() - 1));
        }
        habit.setStreak(computeStreak(habit, today));
    }

    public static int computeStreak(Habit habit, LocalDate referenceDate) {
        if (habit == null || habit.getCompletedDates().isEmpty()) return 0;
        int streak = 0;
        LocalDate cursor = referenceDate;
        while (habit.getCompletedDates().contains(cursor.toString())) {
            streak++;
            cursor = cursor.minusDays(1);
            if (streak > 730) break;
        }
        return streak;
    }
}
