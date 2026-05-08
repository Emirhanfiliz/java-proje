package com.sporttracker.mobile.view;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.CharmListView;
import com.gluonhq.charm.glisten.mvc.View;
import com.google.gson.reflect.TypeToken;
import com.sporttracker.mobile.service.MobileApiService;
import com.sporttracker.mobile.session.MobileSessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class WorkoutListView extends View {

    private final ObservableList<String>  items   = FXCollections.observableArrayList();
    private final CharmListView<String, ?> listView = new CharmListView<>(items);
    private final ProgressIndicator       spinner  = new ProgressIndicator();
    private final Label                   statusLabel = new Label();

    public WorkoutListView() {
        getStylesheets().add(WorkoutListView.class.getResource("/mobile.css").toExternalForm());

        spinner.setPrefSize(36, 36);
        statusLabel.getStyleClass().add("status-label");

        listView.setPlaceholder(new Label("Henüz antrenman kaydı yok"));

        Button addBtn = new Button("+ Antrenman Ekle");
        addBtn.getStyleClass().add("primary-button");
        addBtn.setPrefWidth(220);
        addBtn.setOnAction(e -> getApplication().switchView(com.sporttracker.mobile.MobileApp.ADD_WORKOUT_VIEW));

        VBox content = new VBox(10, spinner, statusLabel, listView, addBtn);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(16));
        VBox.setVgrow(listView, javafx.scene.layout.Priority.ALWAYS);

        setCenter(content);

        showingProperty().addListener((obs, wasShowing, isShowing) -> {
            if (isShowing) loadWorkouts();
        });
    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        MobileSessionManager session = MobileSessionManager.getInstance();
        appBar.setTitleText("Merhaba, " + (session.getUsername() != null ? session.getUsername() : "Kullanıcı"));
        appBar.setNavIcon(null);

        Button logoutBtn = new Button("Çıkış");
        logoutBtn.getStyleClass().add("link-button");
        logoutBtn.setOnAction(e -> {
            MobileSessionManager.getInstance().logout();
            getApplication().switchView(com.sporttracker.mobile.MobileApp.HOME_VIEW);
        });
        appBar.getActionItems().setAll(logoutBtn);
    }

    private void loadWorkouts() {
        spinner.setVisible(true);
        statusLabel.setText("Yükleniyor...");
        items.clear();

        String userId = MobileSessionManager.getInstance().getUserId();
        String token  = MobileSessionManager.getInstance().getToken();

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
                    if (workouts != null) {
                        workouts.forEach(w -> {
                            String name = (String) w.getOrDefault("name", "Antrenman");
                            String date = (String) w.getOrDefault("date", "");
                            String dateShort = date.length() >= 10 ? date.substring(0, 10) : date;
                            items.add(name + (dateShort.isEmpty() ? "" : "  •  " + dateShort));
                        });
                    }
                    if (items.isEmpty()) statusLabel.setText("Henüz antrenman eklemediniz");
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    spinner.setVisible(false);
                    statusLabel.setText("Sunucu bağlantısı yok");
                });
            }
        }, "mobile-load-workouts").start();
    }
}
