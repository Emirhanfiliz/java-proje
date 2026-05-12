package com.sporttracker.desktop.controller;

import com.sporttracker.desktop.api.ApiClient;
import com.sporttracker.desktop.api.ApiResult;
import com.sporttracker.desktop.api.dto.ExerciseItemDto;
import com.sporttracker.desktop.api.dto.WorkoutCreateDto;
import com.sporttracker.desktop.api.dto.WorkoutDto;
import com.sporttracker.desktop.session.SessionManager;
import com.sporttracker.shared.pattern.factory.CalorieStrategyFactory;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class WorkoutModalController extends BaseController {

    @FXML private VBox            modalCard;
    @FXML private TextField       nameField;
    @FXML private TextField       durationField;
    @FXML private ComboBox<String> intensityCombo;
    @FXML private Label           calorieLabel;
    @FXML private TextArea        notesField;
    @FXML private VBox            exercisesContainer;
    @FXML private Label           errorLabel;
    @FXML private Button          saveButton;
    @FXML private Button          cancelButton;

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    public void initialize() {
        intensityCombo.getItems().addAll("Düşük", "Orta", "Yüksek");
        intensityCombo.setValue("Orta");
        durationField.textProperty().addListener((obs, o, n) -> updateCalorieEstimate());
        intensityCombo.valueProperty().addListener((obs, o, n) -> updateCalorieEstimate());
    }

    private void updateCalorieEstimate() {
        String dText = durationField.getText().trim();
        if (dText.isEmpty()) {
            if (calorieLabel != null) calorieLabel.setText("");
            return;
        }
        try {
            int durationMin = Integer.parseInt(dText);
            String intensity = intensityCombo.getValue();
            String key = intensity == null ? "MEDIUM" : switch (intensity) {
                case "Düşük"  -> "LOW";
                case "Yüksek" -> "HIGH";
                default        -> "MEDIUM";
            };
            int kcal = CalorieStrategyFactory.getStrategy(key).calculate(durationMin);
            if (calorieLabel != null) calorieLabel.setText("~" + kcal + " kcal yakılacak");
        } catch (NumberFormatException ignored) {
            if (calorieLabel != null) calorieLabel.setText("");
        }
    }

    @FXML
    public void handleAddExercise() {
        exercisesContainer.getChildren().add(buildExerciseRow());
    }

    private HBox buildExerciseRow() {
        TextField exName   = new TextField(); exName.setPromptText("Egzersiz adı"); exName.setPrefWidth(140);
        TextField exSets   = new TextField(); exSets.setPromptText("Set");          exSets.setPrefWidth(50);
        TextField exReps   = new TextField(); exReps.setPromptText("Tek.");         exReps.setPrefWidth(50);
        TextField exWeight = new TextField(); exWeight.setPromptText("kg");         exWeight.setPrefWidth(55);

        Button deleteBtn = new Button("✕");
        deleteBtn.getStyleClass().add("btn-delete");

        HBox row = new HBox(6, exName, exSets, exReps, exWeight, deleteBtn);
        row.getStyleClass().add("exercise-row");
        deleteBtn.setOnAction(e -> exercisesContainer.getChildren().remove(row));
        return row;
    }

    private List<ExerciseItemDto> collectExercises() {
        List<ExerciseItemDto> list = new ArrayList<>();
        for (var node : exercisesContainer.getChildren()) {
            if (!(node instanceof HBox row)) continue;
            if (row.getChildren().size() < 4) continue;
            try {
                String exName   = ((TextField) row.getChildren().get(0)).getText().trim();
                int    exSets   = Integer.parseInt(((TextField) row.getChildren().get(1)).getText().trim());
                int    exReps   = Integer.parseInt(((TextField) row.getChildren().get(2)).getText().trim());
                double exWeight = Double.parseDouble(((TextField) row.getChildren().get(3)).getText().trim());
                if (!exName.isEmpty()) list.add(new ExerciseItemDto(exName, exSets, exReps, exWeight));
            } catch (NumberFormatException ignored) {}
        }
        return list;
    }

    @FXML
    public void handleSave() {
        clearError(errorLabel);
        String name  = nameField.getText().trim();
        String dText = durationField.getText().trim();

        if (name.isEmpty())  { showError(errorLabel, "Antrenman adı giriniz"); return; }
        if (dText.isEmpty()) { showError(errorLabel, "Süre giriniz"); return; }

        int finalDuration;
        try {
            finalDuration = Integer.parseInt(dText);
        } catch (NumberFormatException e) {
            showError(errorLabel, "Süre sayısal olmalıdır");
            return;
        }

        if (saveButton != null) saveButton.setDisable(true);

        String userId    = SessionManager.getInstance().getUserId();
        String token     = SessionManager.getInstance().getToken();
        String notes     = notesField != null ? notesField.getText().trim() : "";
        List<ExerciseItemDto> exercises = collectExercises();

        Task<ApiResult<WorkoutDto>> task = new Task<>() {
            @Override
            protected ApiResult<WorkoutDto> call() {
                return ApiClient.postWithAuth(
                        "/api/v1/workouts",
                        new WorkoutCreateDto(userId, name, notes, finalDuration, exercises),
                        token,
                        WorkoutDto.class
                );
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            ApiResult<WorkoutDto> result = task.getValue();
            if (result.isSuccess()) {
                if (dashboardController != null) dashboardController.refreshWorkouts();
                closeModal();
            } else {
                if (saveButton != null) saveButton.setDisable(false);
                showError(errorLabel, result.getMessage().isEmpty() ? "Kaydedilemedi" : result.getMessage());
            }
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            if (saveButton != null) saveButton.setDisable(false);
            showError(errorLabel, "Sunucuya bağlanılamadı");
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
