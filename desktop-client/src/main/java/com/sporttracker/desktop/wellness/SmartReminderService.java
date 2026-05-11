package com.sporttracker.desktop.wellness;

import com.sporttracker.shared.wellness.Reminder;
import com.sporttracker.shared.wellness.WellnessData;
import javafx.application.Platform;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Lightweight in-app reminder scheduler.
 *
 * <p>Runs while the desktop app is open. Every 30 seconds it checks the configured reminders
 * against the current local time and notifies the registered callback once per reminder/day.
 */
public final class SmartReminderService {

    private static final SmartReminderService INSTANCE = new SmartReminderService();

    private final ScheduledExecutorService executor;
    private final Set<String> firedToday = new HashSet<>();
    private String firedKey;
    private Consumer<Reminder> listener;
    private ScheduledFuture<?> currentTask;

    private SmartReminderService() {
        executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "smart-reminder-desktop");
            t.setDaemon(true);
            return t;
        });
    }

    public static SmartReminderService getInstance() {
        return INSTANCE;
    }

    public synchronized void start(Consumer<Reminder> listener) {
        this.listener = listener;
        if (currentTask != null && !currentTask.isCancelled()) {
            return;
        }
        currentTask = executor.scheduleAtFixedRate(this::tick, 1, 30, TimeUnit.SECONDS);
    }

    public synchronized void stop() {
        if (currentTask != null) {
            currentTask.cancel(false);
            currentTask = null;
        }
    }

    public synchronized void notifyDataChanged() {
        firedToday.clear();
    }

    private void tick() {
        try {
            String todayKey = LocalDateTime.now().toLocalDate().toString();
            if (!todayKey.equals(firedKey)) {
                firedKey = todayKey;
                firedToday.clear();
            }
            WellnessData data = WellnessStore.load();
            LocalTime now = LocalTime.now();
            for (Reminder reminder : data.getReminders()) {
                if (!reminder.isEnabled() || reminder.getId() == null) continue;
                String dayId = reminder.getId() + "@" + todayKey;
                if (firedToday.contains(dayId)) continue;
                LocalTime target = LocalTime.of(
                        Math.max(0, Math.min(23, reminder.getHour())),
                        Math.max(0, Math.min(59, reminder.getMinute()))
                );
                Duration diff = Duration.between(target, now);
                if (!diff.isNegative() && diff.toMinutes() < 5) {
                    firedToday.add(dayId);
                    Consumer<Reminder> current = listener;
                    if (current != null) {
                        Platform.runLater(() -> current.accept(reminder));
                    }
                }
            }
        } catch (Throwable ignored) {
        }
    }
}
