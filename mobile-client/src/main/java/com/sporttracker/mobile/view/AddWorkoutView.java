package com.sporttracker.mobile.view;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.sporttracker.mobile.service.MobileApiService;
import com.sporttracker.mobile.session.MobileSessionManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class AddWorkoutView extends View {

    private final TextField         nameField     = new TextField();
    private final TextField         durationField = new TextField();
    private final Label             errorLabel    = new Label();
    private final Button            saveButton    = new Button("Kaydet");
    private final ProgressIndicator spinner       = new ProgressIndicator();

    public AddWorkoutView() {
        getStylesheets().add(AddWorkoutView.class.getResource("/mobile.css").toExternalForm());
        getStyleClass().add("workout-detail-view");

        nameField.setPromptText("Antrenman adı (Örn: Kardiyo)");
        nameField.getStyleClass().add("text-field");
        durationField.setPromptText("Süre (dakika)");
        durationField.getStyleClass().add("text-field");
        errorLabel.getStyleClass().add("error-label");
        saveButton.getStyleClass().add("primary-button");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setPrefHeight(48);
        spinner.setPrefSize(28, 28);
        spinner.setVisible(false);

        saveButton.setOnAction(e -> handleSave());

        Label formTitle = new Label("Antrenman Bilgileri");
        formTitle.getStyleClass().add("form-title");
        Label subtitle = new Label("Yeni seansını hızlıca oluştur");
        subtitle.getStyleClass().add("auth-subtitle");

        VBox form = new VBox(12, formTitle, subtitle, errorLabel, nameField, durationField, saveButton, spinner);
        form.getStyleClass().addAll("detail-card", "auth-card");
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(30, 24, 30, 24));
        form.setMaxWidth(380);

        VBox wrapper = new VBox(form);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(20));
        setCenter(wrapper);
    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        appBar.setTitleText("Yeni Antrenman");
        Button backBtn = new Button("← Geri");
        backBtn.getStyleClass().add("link-button");
        backBtn.setOnAction(e -> getApplication().switchView(com.sporttracker.mobile.MobileApp.WORKOUT_VIEW));
        appBar.setNavIcon(backBtn);
    }

    private void handleSave() {
        errorLabel.setText("");
        String name     = nameField.getText().trim();
        String duration = durationField.getText().trim();

        if (name.isEmpty())     { errorLabel.setText("Antrenman adı giriniz"); return; }
        if (duration.isEmpty()) { errorLabel.setText("Süre giriniz"); return; }
        try { Integer.parseInt(duration); }
        catch (NumberFormatException e) { errorLabel.setText("Süre sayısal olmalıdır"); return; }

        saveButton.setDisable(true);
        spinner.setVisible(true);

        String userId = MobileSessionManager.getInstance().getUserId();
        String token  = MobileSessionManager.getInstance().getToken();

        Map<String, Object> body = Map.of(
                "userId",      userId != null ? userId : "",
                "name",        name,
                "description", duration + " dakika",
                "date",        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "durationInMinutes", Integer.parseInt(duration),
                "exercises",   List.of()
        );

        new Thread(() -> {
            try {
                MobileApiService.postAuth("/api/v1/workouts", body, token, Object.class);
                Platform.runLater(() -> getApplication().switchView(com.sporttracker.mobile.MobileApp.WORKOUT_VIEW));
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    saveButton.setDisable(false);
                    spinner.setVisible(false);
                    errorLabel.setText(MobileApiService.toUserMessage(ex, "Antrenman kaydedilemedi. Lütfen tekrar deneyin."));
                });
            }
        }, "mobile-save-workout").start();
    }
}
