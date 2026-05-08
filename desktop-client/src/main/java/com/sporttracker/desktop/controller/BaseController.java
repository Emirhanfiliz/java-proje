package com.sporttracker.desktop.controller;

import com.sporttracker.desktop.session.SessionManager;
import com.sporttracker.desktop.util.ViewManager;
import javafx.scene.control.Label;

public abstract class BaseController {

    protected void showError(Label label, String message) {
        label.setText(message);
        label.setStyle("-fx-text-fill: #ff6b6b;");
    }

    protected void clearError(Label label) {
        label.setText("");
        label.setStyle("");
    }

    protected void showSuccess(Label label, String message) {
        label.setText(message);
        label.setStyle("-fx-text-fill: #51cf66;");
    }

    protected void showInfo(Label label, String message) {
        label.setText(message);
        label.setStyle("-fx-text-fill: #aaa;");
    }

    protected String getToken() {
        return SessionManager.getInstance().getToken();
    }

    protected boolean isLoggedIn() {
        return SessionManager.getInstance().isLoggedIn();
    }

    protected void navigateTo(String viewName) {
        ViewManager.loadView(viewName);
    }
}
