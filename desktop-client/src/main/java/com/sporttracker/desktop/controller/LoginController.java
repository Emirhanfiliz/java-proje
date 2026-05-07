package com.sporttracker.desktop.controller;

import com.sporttracker.desktop.util.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    public void handleLogin() {
        errorLabel.setText("");
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email == null || email.trim().isEmpty()) {
            errorLabel.setText("Email alanı boş bırakılamaz");
            return;
        }

        if (!email.contains("@")) {
            errorLabel.setText("Geçerli bir email adresi giriniz");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            errorLabel.setText("Şifre alanı boş bırakılamaz");
            return;
        }

        ViewManager.loadView("dashboard");
    }

    @FXML
    public void handleGoToRegister() {
        ViewManager.loadView("register");
    }
}
