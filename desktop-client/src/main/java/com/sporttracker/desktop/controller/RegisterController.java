package com.sporttracker.desktop.controller;

import com.sporttracker.desktop.api.ApiClient;
import com.sporttracker.desktop.api.ApiResult;
import com.sporttracker.desktop.api.dto.RegisterRequestDto;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

public class RegisterController extends BaseController {

    @FXML private TextField         usernameField;
    @FXML private TextField         emailField;
    @FXML private PasswordField     passwordField;
    @FXML private PasswordField     confirmPasswordField;
    @FXML private Label             errorLabel;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Button            registerButton;

    @FXML
    public void handleRegister() {
        clearError(errorLabel);
        String username        = usernameField.getText().trim();
        String email           = emailField.getText().trim();
        String password        = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty())                   { showError(errorLabel, "Kullanıcı adı boş bırakılamaz"); return; }
        if (email.isEmpty() || !email.contains("@")) { showError(errorLabel, "Geçerli bir email giriniz"); return; }
        if (password.length() < 6)                { showError(errorLabel, "Şifre en az 6 karakter olmalı"); return; }
        if (!password.equals(confirmPassword))    { showError(errorLabel, "Şifreler eşleşmiyor"); return; }

        setLoading(true);

        Task<ApiResult<Object>> task = new Task<>() {
            @Override
            protected ApiResult<Object> call() {
                return ApiClient.post("/auth/register", new RegisterRequestDto(username, email, password), Object.class);
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            setLoading(false);
            ApiResult<Object> result = task.getValue();
            if (result.isSuccess()) {
                showSuccess(errorLabel, "Kayıt başarılı! Giriş yapabilirsiniz.");
                new Thread(() -> {
                    try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
                    Platform.runLater(() -> navigateTo("login"));
                }).start();
            } else {
                showError(errorLabel, result.getMessage().isEmpty() ? "Kayıt başarısız" : result.getMessage());
            }
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            setLoading(false);
            showError(errorLabel, "Sunucuya bağlanılamadı");
        }));

        new Thread(task, "register-thread").start();
    }

    @FXML
    public void handleGoToLogin() {
        navigateTo("login");
    }

    private void setLoading(boolean loading) {
        if (loadingIndicator != null) loadingIndicator.setVisible(loading);
        if (registerButton   != null) registerButton.setDisable(loading);
    }
}
