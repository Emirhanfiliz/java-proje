package com.sporttracker.mobile.view;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.sporttracker.mobile.session.MobileSessionManager;
import com.sporttracker.mobile.util.CalorieCalculator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.util.Map;

public class WorkoutDetailView extends View {

    private final Label titleLabel = new Label();
    private final Label dateLabel = new Label();
    private final Label durationLabel = new Label();
    private final Label intensityLabel = new Label();
    private final Label caloriesLabel = new Label();

    public WorkoutDetailView() {
        getStylesheets().add(WorkoutDetailView.class.getResource("/mobile.css").toExternalForm());
        getStyleClass().add("workout-detail-view");

        VBox content = buildContent();
        setCenter(content);

        showingProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) loadDetail();
        });
    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        appBar.setTitleText("Antrenman Detayı");
        appBar.setNavIcon(null);
        
        Button backBtn = new Button("Geri");
        backBtn.getStyleClass().add("link-button");
        backBtn.setOnAction(e -> getApplication().switchView(com.sporttracker.mobile.MobileApp.WORKOUT_VIEW));
        appBar.getActionItems().setAll(backBtn);
    }

    private VBox buildContent() {
        VBox card = new VBox(20);
        card.getStyleClass().add("detail-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(30, 20, 30, 20));

        Circle iconCircle = new Circle(40);
        iconCircle.getStyleClass().add("detail-icon-circle");

        titleLabel.getStyleClass().add("detail-title");
        dateLabel.getStyleClass().add("detail-subtitle");

        VBox headerBox = new VBox(8, iconCircle, titleLabel, dateLabel);
        headerBox.setAlignment(Pos.CENTER);

        HBox statsRow = new HBox(15);
        statsRow.setAlignment(Pos.CENTER);

        durationLabel.getStyleClass().add("detail-stat");
        intensityLabel.getStyleClass().add("detail-stat");
        
        statsRow.getChildren().addAll(durationLabel, intensityLabel);

        VBox calorieBox = new VBox(5);
        calorieBox.setAlignment(Pos.CENTER);
        calorieBox.getStyleClass().add("calorie-box");
        Label calTitle = new Label("Yakılan Kalori (Tahmini)");
        calTitle.getStyleClass().add("calorie-title");
        caloriesLabel.getStyleClass().add("calorie-value");
        calorieBox.getChildren().addAll(calTitle, caloriesLabel);

        Button backButton = new Button("Listeye Dön");
        backButton.getStyleClass().add("primary-button");
        backButton.setMaxWidth(Double.MAX_VALUE);
        backButton.setOnAction(e -> getApplication().switchView(com.sporttracker.mobile.MobileApp.WORKOUT_VIEW));

        card.getChildren().addAll(headerBox, statsRow, calorieBox, backButton);
        return card;
    }

    private void loadDetail() {
        Map<String, Object> workout = MobileSessionManager.getInstance().getSelectedWorkout();
        if (workout == null) {
            titleLabel.setText("Antrenman Bulunamadı");
            return;
        }

        String name = (String) workout.getOrDefault("name", "Bilinmeyen Antrenman");
        String date = (String) workout.getOrDefault("date", "-");
        
        Object durObj = workout.get("durationMinutes");
        int duration = 0;
        if (durObj instanceof Number) {
            duration = ((Number) durObj).intValue();
        } else if (durObj instanceof String) {
            try { duration = Integer.parseInt((String) durObj); } catch (Exception ignored) {}
        }
        
        Object intObj = workout.get("intensity");
        String intensity = intObj != null ? String.valueOf(intObj) : "LOW";

        titleLabel.setText(name);
        dateLabel.setText("Tarih: " + date);
        durationLabel.setText("⏱ " + duration + " dk");
        intensityLabel.setText("🔥 " + intensity);

        int calories = CalorieCalculator.calculateCalories(duration, intensity);
        caloriesLabel.setText(calories + " kcal");
    }
}
