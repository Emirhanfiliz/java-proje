package com.sporttracker.mobile.view;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.sporttracker.mobile.service.MobileApiService;
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

public class RegisterView extends View {

    private final TextField         usernameField = new TextField();
    private final TextField         emailField    = new TextField();
    private final PasswordField     passwordField = new PasswordField();
    private final PasswordField     confirmField  = new PasswordField();
    private final Label             errorLabel    = new Label();
    private final Button            registerButton= new Button("Kayıt Ol");
    private final ProgressIndicator spinner       = new ProgressIndicator();

    public RegisterView() {
        getStylesheets().add(RegisterView.class.getResource("/mobile.css").toExternalForm());
        getStyleClass().add("login-view"); // Reusing login styles for consistency

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
        header.setPrefHeight(180);

        Circle bg1 = new Circle(60);
        bg1.getStyleClass().add("header-circle-large");
        StackPane.setAlignment(bg1, Pos.TOP_RIGHT);
        bg1.setTranslateX(30);
        bg1.setTranslateY(-20);

        Circle bg2 = new Circle(40);
        bg2.getStyleClass().add("header-circle-small");
        StackPane.setAlignment(bg2, Pos.BOTTOM_LEFT);
        bg2.setTranslateX(-15);
        bg2.setTranslateY(15);

        Label logoIcon = new Label("🚀");
        logoIcon.getStyleClass().add("logo-icon");

        Label appTitle = new Label("Aramıza Katıl");
        appTitle.getStyleClass().add("app-title");

        VBox logoBox = new VBox(4, logoIcon, appTitle);
        logoBox.setAlignment(Pos.CENTER);
        logoBox.setPadding(new Insets(20, 20, 20, 20));

        header.getChildren().addAll(bg1, bg2, logoBox);
        return header;
    }

    private VBox buildForm() {
        VBox card = new VBox(12);
        card.getStyleClass().addAll("login-card", "auth-card");
        card.setPadding(new Insets(24, 28, 24, 28));
        card.setMaxWidth(380);
        card.setAlignment(Pos.CENTER);

        Label formTitle = new Label("Yeni Hesap Oluştur");
        formTitle.getStyleClass().add("form-title");
        Label formSubtitle = new Label("Dakikalar içinde başla ve gelişimini takip et");
        formSubtitle.getStyleClass().add("auth-subtitle");

        VBox usernameBox = buildFieldGroup("Kullanıcı Adı", usernameField, "email-field");
        usernameField.setPromptText("Kullanıcı adınız");
        
        VBox emailBox = buildFieldGroup("E-Posta", emailField, "email-field");
        emailField.setPromptText("ornek@email.com");

        VBox passwordBox = buildFieldGroup("Şifre", passwordField, "password-field");
        passwordField.setPromptText("••••••••");

        VBox confirmBox = buildFieldGroup("Şifre Tekrar", confirmField, "password-field");
        confirmField.setPromptText("••••••••");

        errorLabel.getStyleClass().add("error-label");
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(300);
        errorLabel.setTextAlignment(TextAlignment.CENTER);

        spinner.getStyleClass().add("login-spinner");
        spinner.setVisible(false);
        spinner.setPrefSize(32, 32);
        spinner.setMaxSize(32, 32);

        registerButton.getStyleClass().add("primary-button");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setPrefHeight(48);
        registerButton.setOnAction(e -> handleRegister());

        Region spacer = new Region();
        spacer.setPrefHeight(4);

        HBox dividerRow = buildDivider();

        Button loginBtn = new Button("Zaten hesabın var mı? Giriş Yap");
        loginBtn.getStyleClass().add("link-button");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setOnAction(e ->
                getApplication().switchView(com.sporttracker.mobile.MobileApp.HOME_VIEW));

        card.getChildren().addAll(
                formTitle,
                formSubtitle,
                usernameBox,
                emailBox,
                passwordBox,
                confirmBox,
                errorLabel,
                spacer,
                registerButton,
                spinner,
                dividerRow,
                loginBtn
        );
        return card;
    }

    private VBox buildFieldGroup(String labelText, Region field, String fieldStyleClass) {
        Label lbl = new Label(labelText);
        lbl.getStyleClass().add("field-label");
        field.getStyleClass().add(fieldStyleClass);
        field.setMaxWidth(Double.MAX_VALUE);
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

    private void handleRegister() {
        errorLabel.setText("");
        String username = usernameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = passwordField.getText();
        String confirm  = confirmField.getText();

        if (username.isEmpty()) { errorLabel.setText("Kullanıcı adı boş olamaz"); shakeField(usernameField); return; }
        if (email.isEmpty() || !email.contains("@")) { errorLabel.setText("Geçerli bir e-posta giriniz"); shakeField(emailField); return; }
        if (password.length() < 6) { errorLabel.setText("Şifre en az 6 karakter olmalı"); shakeField(passwordField); return; }
        if (!password.equals(confirm)) { errorLabel.setText("Şifreler eşleşmiyor"); shakeField(confirmField); return; }

        setLoading(true);

        new Thread(() -> {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> resp = MobileApiService.post(
                        "/auth/register",
                        Map.of("username", username, "email", email, "password", password),
                        Map.class
                );
                
                Platform.runLater(() -> {
                    setLoading(false);
                    errorLabel.getStyleClass().add("success-label");
                    errorLabel.setText("Kayıt başarılı! Giriş sayfasına yönlendiriliyorsunuz...");
                    new Thread(() -> {
                        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
                        Platform.runLater(() -> getApplication().switchView(com.sporttracker.mobile.MobileApp.HOME_VIEW));
                    }).start();
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    errorLabel.getStyleClass().remove("success-label");
                    errorLabel.setText(MobileApiService.toUserMessage(ex, "Kayıt tamamlanamadı. Lütfen tekrar deneyin."));
                    setLoading(false);
                });
            }
        }, "mobile-register-thread").start();
    }

    private void setLoading(boolean loading) {
        registerButton.setDisable(loading);
        spinner.setVisible(loading);
        usernameField.setDisable(loading);
        emailField.setDisable(loading);
        passwordField.setDisable(loading);
        confirmField.setDisable(loading);
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
