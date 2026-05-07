package com.sporttracker.desktop.controller;

import com.sporttracker.desktop.util.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    @FXML
    public void handleRegister() {
        errorLabel.setText("");
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username == null || username.trim().isEmpty()) {
            errorLabel.setText("Kullanıcı adı boş bırakılamaz");
            return;
        }

        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            errorLabel.setText("Geçerli bir email adresi giriniz");
            return;
        }

        if (password == null || password.length() < 6) {
            errorLabel.setText("Şifre en az 6 karakter olmalıdır");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Şifreler eşleşmiyor");
            return;
        }

        ViewManager.loadView("login");
    }

    @FXML
    public void handleGoToLogin() {
        ViewManager.loadView("login");
    }
}
