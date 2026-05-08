package com.sporttracker.desktop.controller;

import com.sporttracker.desktop.api.ApiClient;
import com.sporttracker.desktop.api.ApiResult;
import com.sporttracker.desktop.api.dto.LoginRequestDto;
import com.sporttracker.desktop.api.dto.LoginResponseDto;
import com.sporttracker.desktop.session.SessionManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

public class LoginController extends BaseController {

    @FXML private TextField         emailField;
    @FXML private PasswordField     passwordField;
    @FXML private Label             errorLabel;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Button            loginButton;

    @FXML
    public void handleLogin() {
        clearError(errorLabel);
        String email    = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty()) { showError(errorLabel, "Email boş bırakılamaz"); return; }
        if (!email.contains("@")) { showError(errorLabel, "Geçerli bir email giriniz"); return; }
        if (password.isEmpty()) { showError(errorLabel, "Şifre boş bırakılamaz"); return; }

        setLoading(true);

        Task<ApiResult<LoginResponseDto>> task = new Task<>() {
            @Override
            protected ApiResult<LoginResponseDto> call() {
                return ApiClient.post("/auth/login", new LoginRequestDto(email, password), LoginResponseDto.class);
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            setLoading(false);
            ApiResult<LoginResponseDto> result = task.getValue();
            if (result.isSuccess() && result.getData() != null) {
                LoginResponseDto resp = result.getData();
                SessionManager.getInstance().login(resp.getToken(), resp.getUsername(), resp.getEmail());
                navigateTo("dashboard");
            } else {
                showError(errorLabel, result.getMessage().isEmpty() ? "Giriş başarısız" : result.getMessage());
            }
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            setLoading(false);
            showError(errorLabel, "Sunucuya bağlanılamadı");
        }));

        new Thread(task, "login-thread").start();
    }

    @FXML
    public void handleGoToRegister() {
        navigateTo("register");
    }

    private void setLoading(boolean loading) {
        if (loadingIndicator != null) loadingIndicator.setVisible(loading);
        if (loginButton     != null) loginButton.setDisable(loading);
    }
}
