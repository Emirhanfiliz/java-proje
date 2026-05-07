package com.sporttracker.desktop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class WorkoutModalController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField durationField;

    @FXML
    private Label errorLabel;

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    public void handleSave() {
        errorLabel.setText("");
        String name = nameField.getText();
        String duration = durationField.getText();

        if (name == null || name.trim().isEmpty()) {
            errorLabel.setText("Antrenman adı giriniz.");
            return;
        }

        if (duration == null || duration.trim().isEmpty()) {
            errorLabel.setText("Süre giriniz.");
            return;
        }

        try {
            Integer.parseInt(duration);
        } catch (NumberFormatException e) {
            errorLabel.setText("Süre sayısal olmalıdır.");
            return;
        }

        if (dashboardController != null) {
            dashboardController.addWorkout(name + " - " + duration + " dk");
        }
        closeModal();
    }

    @FXML
    public void handleCancel() {
        closeModal();
    }

    private void closeModal() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
