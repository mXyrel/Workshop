package com.workshop.bbs.controller;

import java.util.Optional;

import com.workshop.bbs.dao.UserDAO;
import com.workshop.bbs.model.User;
import com.workshop.bbs.util.DatabaseConnection;
import com.workshop.bbs.util.Session;
import com.workshop.bbs.view.SceneManager;
import com.workshop.bbs.view.Styles;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * LOGIN SCREEN
 * Scene 1 of 3 — PUP Book Borrowing System
 */
public class LoginController {

    private final VBox root;
    private final TextField usernameField   = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Label messageLabel        = new Label();
    private final Button loginButton        = new Button("Log In");
    private final Label dbStatusLabel       = new Label();

    public LoginController() {
        root = buildView();
    }

    public VBox getView() { return root; }

    private VBox buildView() {
        // ── Outer container ──────────────────────────────────────────
        VBox outer = new VBox();
        outer.setStyle(Styles.ROOT);
        outer.setAlignment(Pos.CENTER);
        outer.setFillWidth(true);

        // ── Header ───────────────────────────────────────────────────
        HBox header = new HBox();
        header.setStyle(Styles.HEADER);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(8);

        Label title    = new Label("PUP Book Borrowing System");
        title.setStyle(Styles.HEADER_TITLE);
        Label subtitle = new Label("Workshop  ·  Might Malinay, BSIT 2-2");
        subtitle.setStyle(Styles.HEADER_SUBTITLE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, spacer, subtitle);

        // ── Login card ───────────────────────────────────────────────
        VBox card = new VBox(14);
        card.setStyle(Styles.CARD);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(32, 40, 32, 40));
        card.setMaxWidth(400);

        Label cardTitle = new Label("Sign In");
        cardTitle.setStyle(Styles.SECTION_TITLE);

        Label cardSub = new Label("Enter your credentials to continue.");
        cardSub.setStyle(Styles.LABEL_MUTED);

        // Username
        Label userLbl = new Label("Username");
        userLbl.setStyle(Styles.LABEL);
        usernameField.setPromptText("username");
        usernameField.setStyle(Styles.INPUT);
        usernameField.setMaxWidth(Double.MAX_VALUE);

        // Password
        Label passLbl = new Label("Password");
        passLbl.setStyle(Styles.LABEL);
        passwordField.setPromptText("••••••••");
        passwordField.setStyle(Styles.INPUT);
        passwordField.setMaxWidth(Double.MAX_VALUE);

        // Message label
        messageLabel.setStyle(Styles.ERROR_LABEL);
        messageLabel.setWrapText(true);

        // Login button
        loginButton.setStyle(Styles.BTN_PRIMARY);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setOnMouseEntered(e -> loginButton.setStyle(Styles.BTN_PRIMARY_HOVER));
        loginButton.setOnMouseExited(e  -> loginButton.setStyle(Styles.BTN_PRIMARY));
        loginButton.setOnAction(e -> handleLogin());

        // Allow Enter key in password field to trigger login
        passwordField.setOnAction(e -> handleLogin());

        // DB status
        checkDbStatus();
        dbStatusLabel.setStyle(Styles.LABEL_MUTED);

        Separator sep = new Separator();

        Label hint = new Label("Default credentials: admin / admin123");
        hint.setStyle(Styles.LABEL_MUTED);

        card.getChildren().addAll(
                cardTitle, cardSub, sep,
                userLbl, usernameField,
                passLbl, passwordField,
                messageLabel,
                loginButton,
                hint,
                dbStatusLabel
        );

        // ── Footer ───────────────────────────────────────────────────
        Label footer = new Label("Polytechnic University of the Philippines  ·  Workshop Project 2026");
        footer.setStyle(Styles.LABEL_MUTED);
        footer.setPadding(new Insets(16, 0, 0, 0));

        // ── Assemble ─────────────────────────────────────────────────
        VBox center = new VBox(card);
        center.setAlignment(Pos.CENTER);
        VBox.setVgrow(center, Priority.ALWAYS);

        outer.getChildren().addAll(header, center, footer);
        VBox.setVgrow(center, Priority.ALWAYS);
        outer.setAlignment(Pos.TOP_CENTER);

        return outer;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setStyle(Styles.ERROR_LABEL);
            messageLabel.setText("Username and password are required.");
            return;
        }

        loginButton.setDisable(true);
        messageLabel.setText("Authenticating…");
        messageLabel.setStyle(Styles.LABEL_MUTED);

        // Run on background thread to avoid blocking UI
        Thread t = new Thread(() -> {
            try {
                UserDAO dao = new UserDAO();
                Optional<User> result = dao.authenticate(username, password);
                javafx.application.Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    if (result.isPresent()) {
                        Session.setUser(result.get());
                        SceneManager.switchTo("dashboard");
                    } else {
                        messageLabel.setStyle(Styles.ERROR_LABEL);
                        messageLabel.setText("Invalid username or password.");
                        passwordField.clear();
                    }
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    messageLabel.setStyle(Styles.ERROR_LABEL);
                    messageLabel.setText("Database error: " + ex.getMessage());
                });
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void checkDbStatus() {
        Thread t = new Thread(() -> {
            boolean ok = DatabaseConnection.testConnection();
            javafx.application.Platform.runLater(() -> {
                if (ok) {
                    dbStatusLabel.setStyle(Styles.SUCCESS_LABEL);
                    dbStatusLabel.setText("● Database connected");
                } else {
                    dbStatusLabel.setStyle(Styles.ERROR_LABEL);
                    dbStatusLabel.setText("● Database not connected — check .env");
                }
            });
        });
        t.setDaemon(true);
        t.start();
    }
}
