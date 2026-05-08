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
            java.net.URL css = ViewManager.class.getResource("/css/dark-theme.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();
        } catch (IOException e) {
            throw new RuntimeException("Error loading FXML", e);
        }
    }
}
