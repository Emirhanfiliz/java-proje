package com.sporttracker.desktop;

import com.sporttracker.desktop.util.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        ViewManager.setPrimaryStage(primaryStage);
        ViewManager.loadView("login");
        primaryStage.setTitle("Sport Tracker");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
