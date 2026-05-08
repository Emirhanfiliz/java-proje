package com.sporttracker.mobile.view;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.CharmListView;
import com.gluonhq.charm.glisten.mvc.View;
import com.google.gson.reflect.TypeToken;
import com.sporttracker.mobile.service.MobileApiService;
import com.sporttracker.mobile.service.StepCounterService;
import com.sporttracker.mobile.session.MobileSessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class WorkoutListView extends View {

    private final ObservableList<Map<String, Object>> items = FXCollections.observableArrayList();
    private final CharmListView<Map<String, Object>, ?> listView = new CharmListView<>(items);
    private final ProgressIndicator spinner = new ProgressIndicator();
    private final Label statusLabel = new Label();

    public WorkoutListView() {
        getStylesheets().add(WorkoutListView.class.getResource("/mobile.css").toExternalForm());
        getStyleClass().add("workout-list-view");

        spinner.setPrefSize(36, 36);
        statusLabel.getStyleClass().add("status-label");

        listView.setPlaceholder(new Label("Henüz antrenman kaydı yok"));
        listView.setCellFactory(p -> new WorkoutListCell());
        listView.getStyleClass().add("workout-list");

        HBox stepCard = buildStepCounterCard();

        Button addBtn = new Button("+ Antrenman Ekle");
        addBtn.getStyleClass().add("primary-button");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setPrefHeight(50);
        addBtn.setOnAction(e -> getApplication().switchView(com.sporttracker.mobile.MobileApp.ADD_WORKOUT_VIEW));

        VBox content = new VBox(12, stepCard, spinner, statusLabel, listView, addBtn);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(16));
        VBox.setVgrow(listView, Priority.ALWAYS);

        setCenter(content);

        listView.selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                MobileSessionManager.getInstance().setSelectedWorkout(newVal);
                getApplication().switchView(com.sporttracker.mobile.MobileApp.WORKOUT_DETAIL_VIEW);
                Platform.runLater(() -> listView.getSelectionModel().clearSelection());
            }
        });

        showingProperty().addListener((obs, wasShowing, isShowing) -> {
            if (isShowing) loadWorkouts();
        });
    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        appBar.setVisible(true);
        MobileSessionManager session = MobileSessionManager.getInstance();
        appBar.setTitleText("Antrenmanlarım");
        appBar.setNavIcon(null);

        Button logoutBtn = new Button("Çıkış");
        logoutBtn.getStyleClass().add("link-button");
        logoutBtn.setStyle("-fx-text-fill: #ff6b8a;");
        logoutBtn.setOnAction(e -> {
            MobileSessionManager.getInstance().logout();
            getApplication().switchView(com.sporttracker.mobile.MobileApp.HOME_VIEW);
        });
        appBar.getActionItems().setAll(logoutBtn);
    }

    private HBox buildStepCounterCard() {
        HBox card = new HBox(16);
        card.getStyleClass().add("step-counter-card");
        card.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("👟");
        icon.getStyleClass().add("step-icon");

        VBox textBox = new VBox(4);
        Label title = new Label("Bugünkü Adımlar");
        title.getStyleClass().add("step-title");
        
        Label countLabel = new Label("0");
        countLabel.getStyleClass().add("step-count");
        countLabel.textProperty().bind(StepCounterService.getInstance().stepCountProperty().asString("%,d"));

        textBox.getChildren().addAll(title, countLabel);
        card.getChildren().addAll(icon, textBox);

        return card;
    }

    private void loadWorkouts() {
        spinner.setVisible(true);
        statusLabel.setText("Yükleniyor...");
        items.clear();

        String userId = MobileSessionManager.getInstance().getUserId();
        String token = MobileSessionManager.getInstance().getToken();

        if (userId == null || token == null) {
            spinner.setVisible(false);
            statusLabel.setText("Oturum bilgisi eksik");
            return;
        }

        new Thread(() -> {
            try {
                Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
                List<Map<String, Object>> workouts = MobileApiService.get(
                        "/api/v1/workouts/user/" + userId, token, listType);
                Platform.runLater(() -> {
                    spinner.setVisible(false);
                    statusLabel.setText("");
                    if (workouts != null && !workouts.isEmpty()) {
                        items.addAll(workouts);
                    } else {
                        statusLabel.setText("Henüz antrenman eklemediniz");
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    spinner.setVisible(false);
                    statusLabel.setText("Sunucu bağlantısı yok veya hata oluştu");
                });
            }
        }, "mobile-load-workouts").start();
    }

    private static class WorkoutListCell extends ListCell<Map<String, Object>> {
        private final VBox card = new VBox();
        private final Label nameLabel = new Label();
        private final Label dateLabel = new Label();
        private final Label durationLabel = new Label();
        private final Label intensityLabel = new Label();

        public WorkoutListCell() {
            card.getStyleClass().add("workout-card");
            
            nameLabel.getStyleClass().add("workout-card-title");
            
            dateLabel.getStyleClass().add("workout-card-subtitle");
            
            durationLabel.getStyleClass().add("workout-card-detail");
            intensityLabel.getStyleClass().add("workout-card-detail");

            HBox detailsBox = new HBox(15, durationLabel, intensityLabel);
            detailsBox.setAlignment(Pos.CENTER_LEFT);

            Region spacer = new Region();
            VBox.setVgrow(spacer, Priority.ALWAYS);

            card.getChildren().addAll(nameLabel, dateLabel, spacer, detailsBox);
            card.setSpacing(6);
            
            setGraphic(card);
        }

        @Override
        protected void updateItem(Map<String, Object> item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else {
                String name = (String) item.getOrDefault("name", "Bilinmeyen Antrenman");
                String date = (String) item.getOrDefault("date", "");
                if (date != null && date.length() >= 10) date = date.substring(0, 10);
                
                Object durationObj = item.get("durationMinutes");
                String duration = durationObj != null ? String.valueOf(durationObj) + " dk" : "-";
                
                Object intensityObj = item.get("intensity");
                String intensity = intensityObj != null ? String.valueOf(intensityObj) : "-";

                nameLabel.setText(name);
                dateLabel.setText("📅 " + date);
                durationLabel.setText("⏱ " + duration);
                intensityLabel.setText("🔥 " + intensity);

                setGraphic(card);
                setText(null);
            }
        }
    }
}
