package com.sporttracker.desktop.controller;

import com.google.gson.reflect.TypeToken;
import com.sporttracker.desktop.api.ApiClient;
import com.sporttracker.desktop.api.ApiResult;
import com.sporttracker.desktop.api.dto.WorkoutDto;
import com.sporttracker.desktop.session.DesktopProfileStore;
import com.sporttracker.desktop.session.SessionManager;
import com.sporttracker.desktop.wellness.SmartReminderService;
import com.sporttracker.desktop.wellness.WellnessStore;
import com.sporttracker.shared.wellness.Reminder;
import com.sporttracker.shared.wellness.WellnessData;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DashboardController extends BaseController {

    @FXML private Label                        welcomeLabel;
    @FXML private Label                        streakLabel;
    @FXML private LineChart<String, Number>    statisticsChart;
    @FXML private ListView<String>             workoutListView;
    @FXML private StackPane                    circularProgressContainer;
    @FXML private ProgressIndicator            loadingIndicator;
    @FXML private Label                        statusLabel;
    @FXML private ProgressBar                  weeklyGoalBar;
    @FXML private Label                        weeklyGoalLabel;
    @FXML private Label                        hydrationLabel;
    @FXML private Label                        weightLabel;
    @FXML private Label                        bmiLabel;
    @FXML private Label                        bmiBadgeLabel;
    @FXML private Label                        activeMinutesLabel;
    @FXML private Label                        caloriesLabel;
    @FXML private Label                        distanceLabel;
    @FXML private Label                        heartRateLabel;
    @FXML private Label                        sleepLabel;

    @FXML private HBox wellnessRow1;
    @FXML private HBox wellnessRow2;
    @FXML private HBox wellnessRow3;
    @FXML private HBox wellnessRow4;
    @FXML private HBox badgeStrip;

    private final ObservableList<String> workoutItems = FXCollections.observableArrayList();
    private final List<String> lastSuccessfulWorkoutItems = new ArrayList<>();
    private int hydrationCurrent = 0;
    private final int hydrationTarget = 8;
    private double currentWeight = 74.0;
    private final double userHeightMeters = 1.75;

    @FXML
    public void initialize() {
        String username = formatDisplayName(
                SessionManager.getInstance().getUsername(),
                SessionManager.getInstance().getEmail()
        );
        DesktopProfileStore.ProfileData profile = DesktopProfileStore.load();
        if (profile.displayName != null && !profile.displayName.isBlank()) {
            username = profile.displayName;
        }
        if (profile.weightKg > 0) {
            currentWeight = profile.weightKg;
        }
        welcomeLabel.setText("Merhaba, " + username + "!");
        streakLabel.setText("🔥 " + (3 + new Random().nextInt(9)) + " Gün");

        workoutListView.setItems(workoutItems);
        buildChart();
        addProgressBar();
        updateHydrationUI();
        updateWeightBmiUI();
        updatePerformancePulse();
        updateWeeklyGoalsUI(0.35);
        loadWorkoutsAsync();
        applyAdaptiveGoalsHint();
        refreshWellnessSections();
        SmartReminderService.getInstance().start(this::showReminder);
    }

    private void applyAdaptiveGoalsHint() {
        try {
            WellnessData data = WellnessStore.load();
            if (data.getAdaptiveGoals() == null) return;
            int goal = data.getAdaptiveGoals().getWeeklyWorkoutGoal();
            if (goal > 0 && weeklyGoalLabel != null) {
                weeklyGoalLabel.setText("Haftalık hedef: " + goal + " antrenman (otomatik ayarlanır)");
            }
        } catch (Throwable ignored) {
        }
    }

    private void refreshWellnessSections() {
        try {
            WellnessData data = WellnessStore.load();
            if (wellnessRow1 != null) {
                wellnessRow1.getChildren().setAll(
                        wrap(WellnessSections.buildPlanCard(data, this::refreshWellnessSections)),
                        wrap(WellnessSections.buildAdaptiveGoalsCard(data, this::refreshWellnessSections)),
                        wrap(WellnessSections.buildTrendCard(data, this::refreshWellnessSections))
                );
            }
            if (wellnessRow2 != null) {
                wellnessRow2.getChildren().setAll(
                        wrap(WellnessSections.buildRecoveryCard(data, this::refreshWellnessSections)),
                        wrap(WellnessSections.buildHrZoneCard(data, this::refreshWellnessSections)),
                        wrap(WellnessSections.buildPrCard(data, this::refreshWellnessSections))
                );
            }
            if (wellnessRow3 != null) {
                wellnessRow3.getChildren().setAll(
                        wrap(WellnessSections.buildHabitCard(data, this::refreshWellnessSections)),
                        wrap(WellnessSections.buildReminderCard(data, this::refreshWellnessSections)),
                        wrap(WellnessSections.buildMealCard(data, this::refreshWellnessSections))
                );
            }
            if (wellnessRow4 != null) {
                wellnessRow4.getChildren().setAll(
                        wrap(WellnessSections.buildChallengeCard(data, this::refreshWellnessSections))
                );
            }
            if (badgeStrip != null) {
                badgeStrip.getChildren().setAll(WellnessSections.buildBadgeStrip(data).getChildren());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private Node wrap(VBox card) {
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }

    private void showReminder(Reminder reminder) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("⏰ Hatırlatıcı");
            alert.setHeaderText(reminder.getLabel());
            alert.setContentText(reminder.getMessage());
            alert.show();
        });
    }

    @FXML
    public void handleNewWorkout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/workout_modal.fxml"));
            Parent root = loader.load();
            WorkoutModalController controller = loader.getController();
            controller.setDashboardController(this);

            Stage modal = new Stage();
            modal.setTitle("Yeni Antrenman");
            modal.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/dark-theme.css").toExternalForm());
            modal.setScene(scene);
            modal.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout() {
        SessionManager.getInstance().logout();
        navigateTo("login");
    }

    @FXML
    public void handleProfile() {
        navigateTo("profile");
    }

    public void addWorkout(String summary) {
        workoutItems.add(0, summary);
    }

    public void refreshWorkouts() {
        loadWorkoutsAsync();
    }

    private void loadWorkoutsAsync() {
        setLoading(true, "Antrenmanlar yükleniyor...");

        String userId = SessionManager.getInstance().getUserId();
        String token  = SessionManager.getInstance().getToken();

        if (userId == null || token == null) {
            setLoading(false, "Oturum bilgisi bulunamadı");
            return;
        }

        Type listType = new TypeToken<List<WorkoutDto>>() {}.getType();

        Task<ApiResult<List<WorkoutDto>>> task = new Task<>() {
            @Override
            protected ApiResult<List<WorkoutDto>> call() {
                return ApiClient.get("/api/v1/workouts/user/" + userId, token, listType);
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            setLoading(false, "");
            ApiResult<List<WorkoutDto>> result = task.getValue();
            workoutItems.clear();
            if (result.isSuccess() && result.getData() != null) {
                result.getData().forEach(w -> workoutItems.add(w.toDisplayString()));
                lastSuccessfulWorkoutItems.clear();
                lastSuccessfulWorkoutItems.addAll(
                        result.getData().stream().map(WorkoutDto::toDisplayString).collect(Collectors.toList())
                );
                if (workoutItems.isEmpty()) showInfo(statusLabel, "Henüz antrenman kaydı yok");
                updateWeeklyGoalsUI(Math.min(1.0, Math.max(0.2, workoutItems.size() / 6.0)));
            } else {
                if (!lastSuccessfulWorkoutItems.isEmpty()) {
                    workoutItems.setAll(lastSuccessfulWorkoutItems);
                    showInfo(statusLabel, "Sunucuya ulaşılamadı. Son senkronize veriler gösteriliyor.");
                    updateWeeklyGoalsUI(Math.min(1.0, Math.max(0.2, workoutItems.size() / 6.0)));
                } else {
                    workoutItems.clear();
                    showInfo(statusLabel, "Yerel mod aktif. Profil ve hedeflerini güncelleyebilirsin.");
                    updateWeeklyGoalsUI(0.0);
                }
            }
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            if (!lastSuccessfulWorkoutItems.isEmpty()) {
                setLoading(false, "Sunucuya ulaşılamadı. Son senkronize veriler gösteriliyor.");
                workoutItems.setAll(lastSuccessfulWorkoutItems);
                updateWeeklyGoalsUI(Math.min(1.0, Math.max(0.2, workoutItems.size() / 6.0)));
            } else {
                setLoading(false, "Yerel mod aktif. Profil ve hedeflerini güncelleyebilirsin.");
                workoutItems.clear();
                updateWeeklyGoalsUI(0.0);
            }
        }));

        new Thread(task, "workout-loader").start();
    }

    private void setLoading(boolean loading, String status) {
        if (loadingIndicator != null) loadingIndicator.setVisible(loading);
        if (statusLabel      != null) showInfo(statusLabel, status);
    }

    private void buildChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Dakika / Gün");
        String[] days   = {"Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz"};
        int[]    values = {45, 60, 0, 90, 45, 120, 30};
        for (int i = 0; i < days.length; i++) {
            series.getData().add(new XYChart.Data<>(days[i], values[i]));
        }
        statisticsChart.getData().add(series);
    }

    private void addProgressBar() {
        com.sporttracker.desktop.component.CircularProgressBar bar =
                new com.sporttracker.desktop.component.CircularProgressBar(40, 8);
        bar.setProgress(65);
        circularProgressContainer.getChildren().add(bar);
    }

    private String formatDisplayName(String username, String email) {
        String candidate = username;
        if (candidate == null || candidate.isBlank()) {
            candidate = email;
        }
        if (candidate == null || candidate.isBlank()) {
            return "Sporcu";
        }
        candidate = candidate.trim();
        if (candidate.contains("@")) {
            int split = candidate.indexOf('@');
            candidate = split > 0 ? candidate.substring(0, split) : "";
        }
        if (candidate.isBlank()) {
            return "Sporcu";
        }
        return Character.toUpperCase(candidate.charAt(0)) + candidate.substring(1);
    }

    @FXML
    public void handleHydrationIncrease() {
        hydrationCurrent = Math.min(hydrationTarget, hydrationCurrent + 1);
        updateHydrationUI();
    }

    @FXML
    public void handleHydrationDecrease() {
        hydrationCurrent = Math.max(0, hydrationCurrent - 1);
        updateHydrationUI();
    }

    @FXML
    public void handleWeightIncrease() {
        currentWeight = Math.min(220.0, currentWeight + 0.5);
        updateWeightBmiUI();
    }

    @FXML
    public void handleWeightDecrease() {
        currentWeight = Math.max(35.0, currentWeight - 0.5);
        updateWeightBmiUI();
    }

    private void updateWeeklyGoalsUI(double progress) {
        if (weeklyGoalBar == null || weeklyGoalLabel == null) return;
        double clamped = Math.max(0.0, Math.min(1.0, progress));
        int remaining = (int) Math.round((1.0 - clamped) * 100);
        weeklyGoalBar.setProgress(clamped);
        weeklyGoalLabel.setText("Haftalık hedefe %" + remaining + " kaldı");
    }

    private void updateHydrationUI() {
        if (hydrationLabel != null) {
            hydrationLabel.setText(hydrationCurrent + "/" + hydrationTarget + " Bardak");
        }
    }

    private void updateWeightBmiUI() {
        if (weightLabel == null || bmiLabel == null || bmiBadgeLabel == null) return;

        double bmi = currentWeight / (userHeightMeters * userHeightMeters);
        String badge;
        String styleClass;
        if (bmi < 18.5) {
            badge = "Zayıf";
            styleClass = "bmi-under-desktop";
        } else if (bmi < 25.0) {
            badge = "Normal";
            styleClass = "bmi-normal-desktop";
        } else {
            badge = "Fazla Kilolu";
            styleClass = "bmi-over-desktop";
        }

        bmiBadgeLabel.getStyleClass().removeAll("bmi-under-desktop", "bmi-normal-desktop", "bmi-over-desktop");
        bmiBadgeLabel.getStyleClass().add(styleClass);
        weightLabel.setText(String.format("%.1f kg", currentWeight));
        bmiLabel.setText(String.format("BMI: %.1f", bmi));
        bmiBadgeLabel.setText(badge);
    }

    private void updatePerformancePulse() {
        String seedKey = SessionManager.getInstance().getUserId();
        if (seedKey == null || seedKey.isBlank()) {
            seedKey = SessionManager.getInstance().getUsername();
        }
        int seed = seedKey != null ? Math.abs(seedKey.hashCode()) : 17;
        Random seededRandom = new Random(seed + java.time.LocalDate.now().toEpochDay());

        int activeMinutes = 30 + seededRandom.nextInt(80);
        int calories = 320 + seededRandom.nextInt(580);
        double distance = 2.0 + seededRandom.nextDouble() * 8.5;
        int heartRate = 54 + seededRandom.nextInt(24);
        int sleepHours = 5 + seededRandom.nextInt(4);
        int sleepMinutes = seededRandom.nextInt(6) * 10;

        if (activeMinutesLabel != null) activeMinutesLabel.setText(activeMinutes + " dk");
        if (caloriesLabel != null) caloriesLabel.setText(calories + " kcal");
        if (distanceLabel != null) distanceLabel.setText(String.format("%.1f km", distance));
        if (heartRateLabel != null) heartRateLabel.setText(heartRate + " bpm");
        if (sleepLabel != null) sleepLabel.setText(String.format("%d sa %02d dk", sleepHours, sleepMinutes));
    }
}
