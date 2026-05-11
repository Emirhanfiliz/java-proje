package com.sporttracker.mobile.view;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.sporttracker.mobile.MobileApp;
import com.sporttracker.mobile.session.MobileProfileStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ProfileView extends View {

    private final Label statusLabel = new Label();
    private final Label avatarPreview = new Label("🏃");
    private final TextField avatarField = new TextField();
    private final TextField displayNameField = new TextField();
    private final TextField heightField = new TextField();
    private final TextField weightField = new TextField();
    private final TextField dailyGoalField = new TextField();

    public ProfileView() {
        getStylesheets().add(ProfileView.class.getResource("/mobile.css").toExternalForm());
        getStyleClass().add("workout-detail-view");
        setCenter(buildLayout());
        loadProfile();
    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        appBar.setTitleText("Profilim");
        Button backBtn = new Button("Geri");
        backBtn.getStyleClass().add("link-button");
        backBtn.setOnAction(e -> getApplication().switchView(MobileApp.WORKOUT_VIEW));
        appBar.setNavIcon(backBtn);
    }

    private VBox buildLayout() {
        VBox card = new VBox(12);
        card.getStyleClass().addAll("detail-card", "auth-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(20));
        card.setMaxWidth(380);

        Label title = new Label("Profil Bilgileri");
        title.getStyleClass().add("form-title");

        avatarPreview.getStyleClass().add("profile-avatar");
        avatarField.getStyleClass().add("text-field");
        displayNameField.getStyleClass().add("text-field");
        heightField.getStyleClass().add("text-field");
        weightField.getStyleClass().add("text-field");
        dailyGoalField.getStyleClass().add("text-field");

        avatarField.setPromptText("Avatar (emoji)");
        displayNameField.setPromptText("Görünen ad");
        heightField.setPromptText("Boy (cm)");
        weightField.setPromptText("Kilo (kg)");
        dailyGoalField.setPromptText("Günlük adım hedefi");

        HBox avatarRow = new HBox(10, new Label("Profil Foto"), avatarPreview, avatarField);
        avatarRow.setAlignment(Pos.CENTER_LEFT);

        statusLabel.getStyleClass().add("status-label");

        Button saveBtn = new Button("Kaydet");
        saveBtn.getStyleClass().add("primary-button");
        saveBtn.setOnAction(e -> saveProfile());

        card.getChildren().addAll(
                title,
                avatarRow,
                labeledField("Görünen Ad", displayNameField),
                labeledField("Boy", heightField),
                labeledField("Kilo", weightField),
                labeledField("Günlük Hedef", dailyGoalField),
                saveBtn,
                statusLabel
        );

        VBox wrapper = new VBox(card);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(18));
        return wrapper;
    }

    private VBox labeledField(String label, TextField field) {
        Label l = new Label(label);
        l.getStyleClass().add("field-label");
        return new VBox(4, l, field);
    }

    private void loadProfile() {
        MobileProfileStore.ProfileData data = MobileProfileStore.load();
        avatarField.setText(data.avatar);
        avatarPreview.setText(data.avatar);
        displayNameField.setText(data.displayName);
        heightField.setText(String.format("%.1f", data.heightCm));
        weightField.setText(String.format("%.1f", data.weightKg));
        dailyGoalField.setText(String.valueOf(data.dailyGoal));
        avatarField.textProperty().addListener((obs, oldV, newV) -> avatarPreview.setText(newV == null || newV.isBlank() ? "🏃" : newV));
    }

    private void saveProfile() {
        try {
            String avatar = avatarField.getText().isBlank() ? "🏃" : avatarField.getText().trim();
            String displayName = displayNameField.getText().trim();
            double height = Double.parseDouble(heightField.getText().trim());
            double weight = Double.parseDouble(weightField.getText().trim());
            int dailyGoal = Integer.parseInt(dailyGoalField.getText().trim());

            MobileProfileStore.save(new MobileProfileStore.ProfileData(avatar, displayName, height, weight, dailyGoal));
            statusLabel.setText("Profil kaydedildi.");
        } catch (Exception ex) {
            statusLabel.setText("Bilgileri kontrol et (boy/kilo/hedef sayısal olmalı).");
        }
    }
}
