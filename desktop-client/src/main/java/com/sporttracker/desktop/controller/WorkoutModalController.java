package com.sporttracker.desktop.controller;

import com.sporttracker.desktop.api.ApiClient;
import com.sporttracker.desktop.api.ApiResult;
import com.sporttracker.desktop.api.dto.WorkoutCreateDto;
import com.sporttracker.desktop.api.dto.WorkoutDto;
import com.sporttracker.desktop.session.SessionManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class WorkoutModalController extends BaseController {

    @FXML private TextField nameField;
    @FXML private TextField durationField;
    @FXML private Label     errorLabel;
    @FXML private Button    saveButton;

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    public void handleSave() {
        clearError(errorLabel);
        String name     = nameField.getText().trim();
        String duration = durationField.getText().trim();

        if (name.isEmpty())     { showError(errorLabel, "Antrenman adı giriniz"); return; }
        if (duration.isEmpty()) { showError(errorLabel, "Süre giriniz"); return; }

        try { Integer.parseInt(duration); }
        catch (NumberFormatException e) { showError(errorLabel, "Süre sayısal olmalıdır"); return; }

        if (saveButton != null) saveButton.setDisable(true);

        String userId      = SessionManager.getInstance().getUserId();
        String token       = SessionManager.getInstance().getToken();
        String description = duration + " dakika";

        Task<ApiResult<WorkoutDto>> task = new Task<>() {
            @Override
            protected ApiResult<WorkoutDto> call() {
                return ApiClient.postWithAuth(
                        "/api/v1/workouts",
                        new WorkoutCreateDto(userId, name, description),
                        token,
                        WorkoutDto.class
                );
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            ApiResult<WorkoutDto> result = task.getValue();
            if (result.isSuccess()) {
                if (dashboardController != null) {
                    dashboardController.addWorkout(name + " - " + duration + " dk");
                }
                closeModal();
            } else {
                if (saveButton != null) saveButton.setDisable(false);
                showError(errorLabel, result.getMessage().isEmpty() ? "Kaydedilemedi" : result.getMessage());
            }
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            if (saveButton != null) saveButton.setDisable(false);
            // API çağrısı başarısız olsa bile local olarak ekle (offline mod)
            if (dashboardController != null) dashboardController.addWorkout(name + " - " + duration + " dk");
            closeModal();
        }));

        new Thread(task, "save-workout").start();
    }

    @FXML
    public void handleCancel() {
        closeModal();
    }

    private void closeModal() {
        ((Stage) nameField.getScene().getWindow()).close();
    }
}
