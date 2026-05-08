package com.sporttracker.desktop.controller;

import com.google.gson.reflect.TypeToken;
import com.sporttracker.desktop.api.ApiClient;
import com.sporttracker.desktop.api.ApiResult;
import com.sporttracker.desktop.api.dto.WorkoutDto;
import com.sporttracker.desktop.session.SessionManager;
import javafx.application.Platform;
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
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.lang.reflect.Type;
import java.util.List;

public class DashboardController extends BaseController {

    @FXML private Label                        welcomeLabel;
    @FXML private LineChart<String, Number>    statisticsChart;
    @FXML private ListView<String>             workoutListView;
    @FXML private StackPane                    circularProgressContainer;
    @FXML private ProgressIndicator            loadingIndicator;
    @FXML private Label                        statusLabel;

    private final ObservableList<String> workoutItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        String username = SessionManager.getInstance().getUsername();
        welcomeLabel.setText("Hoş geldin, " + (username != null ? username : "Kullanıcı") + "!");

        workoutListView.setItems(workoutItems);
        buildChart();
        addProgressBar();
        loadWorkoutsAsync();
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
                if (workoutItems.isEmpty()) showInfo(statusLabel, "Henüz antrenman kaydı yok");
            } else {
                showInfo(statusLabel, "Sunucu bağlantısı yok — çevrimdışı mod");
            }
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            setLoading(false, "Yükleme başarısız");
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
}
