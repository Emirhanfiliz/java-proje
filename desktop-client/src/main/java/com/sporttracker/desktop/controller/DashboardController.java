package com.sporttracker.desktop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {
        welcomeLabel.setText("Sport Tracker Dashboard'a Hoş Geldiniz");
    }
}
