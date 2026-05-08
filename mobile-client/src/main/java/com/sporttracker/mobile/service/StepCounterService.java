package com.sporttracker.mobile.service;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Random;

/**
 * Native API Simulation for Step Counting.
 * Gerçek cihazlarda Gluon Attach (ör. Device/Position) kullanılır.
 * Bu sınıf şimdilik basit bir thread ile adım sayımını simüle eder.
 */
public class StepCounterService {

    private static volatile StepCounterService instance;
    private final IntegerProperty stepCount = new SimpleIntegerProperty(0);
    private final Random random = new Random();
    private boolean isRunning = false;

    private StepCounterService() {
        startSimulation();
    }

    public static StepCounterService getInstance() {
        if (instance == null) {
            synchronized (StepCounterService.class) {
                if (instance == null) instance = new StepCounterService();
            }
        }
        return instance;
    }

    public IntegerProperty stepCountProperty() {
        return stepCount;
    }

    public int getStepCount() {
        return stepCount.get();
    }

    private void startSimulation() {
        if (isRunning) return;
        isRunning = true;

        // Başlangıç adım sayısı (günün o saatine kadar atılmış adımlar gibi)
        Platform.runLater(() -> stepCount.set(random.nextInt(3000) + 1000));

        Thread stepThread = new Thread(() -> {
            while (isRunning) {
                try {
                    // Her 2 ila 5 saniyede bir 1-5 arası adım ekle
                    Thread.sleep(2000 + random.nextInt(3000));
                    int current = stepCount.get();
                    int increment = 1 + random.nextInt(5);
                    Platform.runLater(() -> stepCount.set(current + increment));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    isRunning = false;
                }
            }
        });
        stepThread.setDaemon(true);
        stepThread.start();
    }
}
