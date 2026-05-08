package com.sporttracker.desktop.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private LineChart<String, Number> statisticsChart;

    @FXML
    private ListView<String> workoutListView;

    @FXML
    private StackPane circularProgressContainer;

    private ObservableList<String> workouts = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        welcomeLabel.setText("Sport Tracker Dashboard'a Hoş Geldiniz");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Dakika / Gün");
        series.getData().add(new XYChart.Data<>("Pzt", 45));
        series.getData().add(new XYChart.Data<>("Sal", 60));
        series.getData().add(new XYChart.Data<>("Çar", 0));
        series.getData().add(new XYChart.Data<>("Per", 90));
        series.getData().add(new XYChart.Data<>("Cum", 45));
        series.getData().add(new XYChart.Data<>("Cmt", 120));
        series.getData().add(new XYChart.Data<>("Paz", 30));
        statisticsChart.getData().add(series);

        workouts.addAll("Göğüs & Arka Kol - 60 dk", "Sırt & Biceps - 50 dk", "Bacak - 70 dk");
        workoutListView.setItems(workouts);

        com.sporttracker.desktop.component.CircularProgressBar intensityBar = new com.sporttracker.desktop.component.CircularProgressBar(40, 8);
        intensityBar.setProgress(65);
        circularProgressContainer.getChildren().add(intensityBar);
    }

    @FXML
    public void handleNewWorkout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/workout_modal.fxml"));
            Parent root = loader.load();

            WorkoutModalController controller = loader.getController();
            controller.setDashboardController(this);

            Stage modalStage = new Stage();
            modalStage.setTitle("Yeni Antrenman");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addWorkout(String workoutSummary) {
        workouts.add(0, workoutSummary);
    }
}
