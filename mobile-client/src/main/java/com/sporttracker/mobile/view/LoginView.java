package com.sporttracker.mobile.view;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.sporttracker.mobile.service.MobileApiService;
import com.sporttracker.mobile.session.MobileSessionManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.Map;

public class LoginView extends View {

    private final TextField         emailField    = new TextField();
    private final PasswordField     passwordField = new PasswordField();
    private final Label             errorLabel    = new Label();
    private final Button            loginButton   = new Button("Giriş Yap");
    private final ProgressIndicator spinner       = new ProgressIndicator();

    public LoginView() {
        getStylesheets().add(LoginView.class.getResource("/mobile.css").toExternalForm());

        emailField.setPromptText("Email");
        passwordField.setPromptText("Şifre");
        errorLabel.getStyleClass().add("error-label");
        loginButton.getStyleClass().add("primary-button");
        loginButton.setPrefWidth(220);
        spinner.setVisible(false);
        spinner.setPrefSize(28, 28);

        Label title = new Label("Sport Tracker");
        title.getStyleClass().add("title-label");

        Label subtitle = new Label("Mobil Uygulama");
        subtitle.getStyleClass().add("subtitle-label");

        Button registerBtn = new Button("Hesap Oluştur");
        registerBtn.getStyleClass().add("link-button");
        registerBtn.setOnAction(e -> getApplication().switchView(com.sporttracker.mobile.MobileApp.REGISTER_VIEW));

        loginButton.setOnAction(e -> handleLogin());

        VBox form = new VBox(12, title, subtitle, errorLabel, emailField, passwordField, loginButton, spinner, registerBtn);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(40, 30, 40, 30));
        form.setMaxWidth(340);

        setCenter(form);
    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        appBar.setTitleText("Sport Tracker");
        appBar.setNavIcon(null);
    }

    private void handleLogin() {
        errorLabel.setText("");
        String email    = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || !email.contains("@")) { errorLabel.setText("Geçerli bir email giriniz"); return; }
        if (password.isEmpty()) { errorLabel.setText("Şifre boş bırakılamaz"); return; }

        loginButton.setDisable(true);
        spinner.setVisible(true);

        new Thread(() -> {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> resp = MobileApiService.post(
                        "/auth/login",
                        Map.of("email", email, "password", password),
                        Map.class
                );
                String token    = (String) resp.getOrDefault("token", "");
                String username = (String) resp.getOrDefault("username", email);
                MobileSessionManager.getInstance().login(token, username, email);
                Platform.runLater(() -> getApplication().switchView(com.sporttracker.mobile.MobileApp.WORKOUT_VIEW));
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    errorLabel.setText("Giriş başarısız: " + ex.getMessage());
                    loginButton.setDisable(false);
                    spinner.setVisible(false);
                });
            }
        }, "mobile-login").start();
    }
}
