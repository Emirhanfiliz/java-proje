package com.sporttracker.mobile.view;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.sporttracker.mobile.service.MobileApiService;
import com.sporttracker.mobile.session.MobileSessionManager;
import com.sporttracker.shared.util.AuthValidator;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;

import java.util.Map;

public class LoginView extends View {

    private final TextField         emailField    = new TextField();
    private final PasswordField     passwordField = new PasswordField();
    private final Label             errorLabel    = new Label();
    private final Button            loginButton   = new Button("Giriş Yap");
    private final ProgressIndicator spinner       = new ProgressIndicator();

    public LoginView() {
        getStylesheets().add(LoginView.class.getResource("/mobile.css").toExternalForm());
        getStyleClass().add("login-view");

        setCenter(buildLayout());
    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        appBar.setVisible(false);
    }

    private ScrollPane buildLayout() {
        VBox root = new VBox(0);
        root.setAlignment(Pos.TOP_CENTER);
        root.getStyleClass().add("login-root");

        StackPane header = buildHeader();
        VBox      form   = buildForm();

        VBox.setMargin(form, new Insets(-30, 0, 0, 0));

        root.getChildren().addAll(header, form);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("auth-scroll");
        return scrollPane;
    }

    private StackPane buildHeader() {
        StackPane header = new StackPane();
        header.getStyleClass().add("login-header");
        header.setPrefHeight(240);

        Circle bg1 = new Circle(80);
        bg1.getStyleClass().add("header-circle-large");
        StackPane.setAlignment(bg1, Pos.TOP_RIGHT);
        bg1.setTranslateX(30);
        bg1.setTranslateY(-20);

        Circle bg2 = new Circle(50);
        bg2.getStyleClass().add("header-circle-small");
        StackPane.setAlignment(bg2, Pos.BOTTOM_LEFT);
        bg2.setTranslateX(-15);
        bg2.setTranslateY(15);

        Label logoIcon = new Label("🏋");
        logoIcon.getStyleClass().add("logo-icon");

        Label appTitle = new Label("Sport Tracker");
        appTitle.getStyleClass().add("app-title");

        Label appSlogan = new Label("Performansını takip et, sınırlarını aş");
        appSlogan.getStyleClass().add("app-slogan");
        appSlogan.setWrapText(true);
        appSlogan.setTextAlignment(TextAlignment.CENTER);

        VBox logoBox = new VBox(8, logoIcon, appTitle, appSlogan);
        logoBox.setAlignment(Pos.CENTER);
        logoBox.setPadding(new Insets(40, 20, 20, 20));

        header.getChildren().addAll(bg1, bg2, logoBox);
        return header;
    }

    private VBox buildForm() {
        VBox card = new VBox(16);
        card.getStyleClass().addAll("login-card", "auth-card");
        card.setPadding(new Insets(36, 28, 32, 28));
        card.setMaxWidth(380);
        card.setAlignment(Pos.CENTER);

        Label formTitle = new Label("Hesabına Giriş Yap");
        formTitle.getStyleClass().add("form-title");
        Label formSubtitle = new Label("Antrenmanlarını takip etmek için giriş yap");
        formSubtitle.getStyleClass().add("auth-subtitle");

        VBox emailBox = buildFieldGroup("E-Posta", emailField, "email-field");
        emailField.setPromptText("ornek@email.com");
        emailField.setMaxWidth(Double.MAX_VALUE);

        VBox passwordBox = buildFieldGroup("Şifre", passwordField, "password-field");
        passwordField.setPromptText("••••••••");
        passwordField.setMaxWidth(Double.MAX_VALUE);

        errorLabel.getStyleClass().add("error-label");
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(300);
        errorLabel.setTextAlignment(TextAlignment.CENTER);

        spinner.getStyleClass().add("login-spinner");
        spinner.setVisible(false);
        spinner.setPrefSize(32, 32);
        spinner.setMaxSize(32, 32);

        loginButton.getStyleClass().add("primary-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(48);
        loginButton.setOnAction(e -> handleLogin());

        Region spacer = new Region();
        spacer.setPrefHeight(4);

        HBox dividerRow = buildDivider();

        Button registerBtn = new Button("Hesabın yok mu? Kayıt Ol");
        registerBtn.getStyleClass().add("link-button");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setOnAction(e ->
                getApplication().switchView(com.sporttracker.mobile.MobileApp.REGISTER_VIEW));

        card.getChildren().addAll(
                formTitle,
                formSubtitle,
                emailBox,
                passwordBox,
                errorLabel,
                spacer,
                loginButton,
                spinner,
                dividerRow,
                registerBtn
        );
        return card;
    }

    private VBox buildFieldGroup(String labelText, Region field, String fieldStyleClass) {
        Label lbl = new Label(labelText);
        lbl.getStyleClass().add("field-label");
        field.getStyleClass().add(fieldStyleClass);
        VBox group = new VBox(6, lbl, field);
        group.setMaxWidth(Double.MAX_VALUE);
        return group;
    }

    private HBox buildDivider() {
        Region left  = new Region();
        Region right = new Region();
        left.setPrefHeight(1);
        right.setPrefHeight(1);
        left.setMaxWidth(Double.MAX_VALUE);
        right.setMaxWidth(Double.MAX_VALUE);
        left.getStyleClass().add("divider-line");
        right.getStyleClass().add("divider-line");
        HBox.setHgrow(left,  javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(right, javafx.scene.layout.Priority.ALWAYS);
        Label orLabel = new Label("  veya  ");
        orLabel.getStyleClass().add("or-label");
        HBox row = new HBox(8, left, orLabel, right);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    private void handleLogin() {
        errorLabel.setText("");
        String email    = emailField.getText().trim();
        String password = passwordField.getText();

        String validationError = AuthValidator.validateLogin(email, password);
        if (validationError != null) {
            errorLabel.setText(validationError);
            shakeField(validationError.contains("mail") ? emailField : passwordField);
            return;
        }

        setLoading(true);

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
                Platform.runLater(() ->
                        getApplication().switchView(com.sporttracker.mobile.MobileApp.WORKOUT_VIEW));
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    errorLabel.getStyleClass().remove("success-label");
                    errorLabel.setText(MobileApiService.toUserMessage(ex, "Giriş yapılamadı. Lütfen tekrar deneyin."));
                    setLoading(false);
                });
            }
        }, "mobile-login-thread").start();
    }

    private void setLoading(boolean loading) {
        loginButton.setDisable(loading);
        spinner.setVisible(loading);
        emailField.setDisable(loading);
        passwordField.setDisable(loading);
    }

    private void shakeField(Region field) {
        javafx.animation.TranslateTransition shake =
                new javafx.animation.TranslateTransition(javafx.util.Duration.millis(60), field);
        shake.setFromX(0);
        shake.setByX(8);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }
}
