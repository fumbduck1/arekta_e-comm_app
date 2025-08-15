package com.arektaecomm.controller;

import com.arektaecomm.dao.FirebaseUserDao;
import com.arektaecomm.model.User;
import com.arektaecomm.service.AuthService;
import com.arektaecomm.util.NavigationManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.InputStream;
import java.util.Properties;

public class SignupController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField pwdField;
    @FXML
    private Button registerButton;
    @FXML
    private Hyperlink backLink;
    @FXML
    private Button togglePwdBtn;
    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;

    private final AuthService authService = AuthService.getInstance();
    private boolean pwdVisible = false;
    private TextField pwdVisibleField = new TextField();

    @FXML
    private void onSignup() {
        String email = emailField.getText().trim();
        String password = pwdField.getText();
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "All fields are required.");
            return;
        }

        registerButton.setDisable(true);
        authService.signUp(email, password).whenComplete((userRecord, ex) -> {
            Platform.runLater(() -> {
                registerButton.setDisable(false);
                if (ex != null) {
                    showAlert(Alert.AlertType.ERROR, "Sign up failed: " + ex.getMessage());
                } else {
                    // Save user info to RTDB
                    String userId = email.replace("@", "_").replace(".", "_");
                    User user = new User(userId, email, "user", name, phone, "", "");
                    new FirebaseUserDao(getFirebaseUrl()).updateUser(user);
                    showAlert(Alert.AlertType.INFORMATION,
                            "Registration successful for " + userRecord.get("email").asText());
                    NavigationManager.clearHistory();
                    NavigationManager.navigateTo("/com/arektaecomm/fxml/home.fxml");
                }
            });
        });
    }

    @FXML
    private void onBack() throws Exception {
        Stage stage = (Stage) emailField.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/arektaecomm/fxml/login.fxml"));
        stage.getScene().setRoot(loader.load());
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.initOwner(registerButton.getScene().getWindow());
        alert.showAndWait();
    }

    @FXML
    private void togglePasswordVisibility() {
        HBox parent = null;
        try {
            parent = (HBox) pwdField.getParent();
        } catch (Exception e) {
            parent = null;
        }
        if (parent == null) {
            // Defensive: show error or ignore
            System.err.println("Password field parent is null. Cannot toggle visibility.");
            return;
        }
        if (!pwdVisible) {
            // Unbind if previously bound
            pwdVisibleField.textProperty().unbindBidirectional(pwdField.textProperty());
            pwdVisibleField.setText(pwdField.getText());
            pwdVisibleField.setPromptText("Password");
            pwdVisibleField.setMaxWidth(320);
            pwdVisibleField.setStyle(pwdField.getStyle());
            pwdVisibleField.getStyleClass().setAll(pwdField.getStyleClass());
            // Bind bidirectionally
            pwdVisibleField.textProperty().bindBidirectional(pwdField.textProperty());
            parent.getChildren().set(0, pwdVisibleField);
            togglePwdBtn.setText("🙈");
            pwdVisibleField.requestFocus();
            pwdVisibleField.positionCaret(pwdVisibleField.getText().length());
        } else {
            pwdVisibleField.textProperty().unbindBidirectional(pwdField.textProperty());
            parent.getChildren().set(0, pwdField);
            togglePwdBtn.setText("👁️");
            pwdField.requestFocus();
            pwdField.positionCaret(pwdField.getText().length());
        }
        pwdVisible = !pwdVisible;
    }

    private String getFirebaseUrl() {
        try (InputStream in = getClass().getResourceAsStream("/config.properties")) {
            Properties props = new Properties();
            props.load(in);
            return props.getProperty("firebase.databaseURL");
        } catch (Exception e) {
            return "https://YOUR_PROJECT_ID.firebaseio.com/";
        }
    }
}