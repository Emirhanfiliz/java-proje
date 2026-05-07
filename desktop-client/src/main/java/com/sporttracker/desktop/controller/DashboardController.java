package com.sporttracker.desktop.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private LineChart<String, Number> statisticsChart;

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
    }
}
