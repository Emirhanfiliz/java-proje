package com.sporttracker.desktop.controller;

import com.sporttracker.desktop.session.DesktopProfileStore;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ProfileController extends BaseController {

    @FXML private Label avatarPreview;
    @FXML private Label statusLabel;
    @FXML private TextField avatarField;
    @FXML private TextField displayNameField;
    @FXML private TextField heightField;
    @FXML private TextField weightField;
    @FXML private TextField dailyGoalField;

    @FXML
    public void initialize() {
        DesktopProfileStore.ProfileData data = DesktopProfileStore.load();
        avatarField.setText(data.avatar);
        avatarPreview.setText(data.avatar);
        displayNameField.setText(data.displayName);
        heightField.setText(String.format("%.1f", data.heightCm));
        weightField.setText(String.format("%.1f", data.weightKg));
        dailyGoalField.setText(String.valueOf(data.dailyGoal));
        avatarField.textProperty().addListener((obs, oldV, newV) -> avatarPreview.setText(newV == null || newV.isBlank() ? "🏃" : newV));
    }

    @FXML
    public void handleSave() {
        try {
            String avatar = avatarField.getText().isBlank() ? "🏃" : avatarField.getText().trim();
            String displayName = displayNameField.getText().trim();
            double height = Double.parseDouble(heightField.getText().trim());
            double weight = Double.parseDouble(weightField.getText().trim());
            int dailyGoal = Integer.parseInt(dailyGoalField.getText().trim());

            DesktopProfileStore.save(new DesktopProfileStore.ProfileData(avatar, displayName, height, weight, dailyGoal));
            showSuccess(statusLabel, "Profil kaydedildi.");
        } catch (Exception ex) {
            showError(statusLabel, "Boy/kilo/hedef alanlarını sayısal giriniz.");
        }
    }

    @FXML
    public void handleBack() {
        navigateTo("dashboard");
    }
}
