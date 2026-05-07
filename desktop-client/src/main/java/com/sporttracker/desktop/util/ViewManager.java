package com.sporttracker.desktop.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ViewManager {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void loadView(String fxmlName) {
        try {
            URL url = ViewManager.class.getResource("/fxml/" + fxmlName + ".fxml");
            if (url == null) {
                throw new RuntimeException("FXML file not found: " + fxmlName);
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException("Error loading FXML", e);
        }
    }
}
