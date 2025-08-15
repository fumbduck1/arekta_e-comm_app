package com.arektaecomm.controller;

import com.arektaecomm.service.AuthService;
import com.arektaecomm.util.NavigationManager;
import com.arektaecomm.dao.FirebaseUserDao;
import com.arektaecomm.model.User;

import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Properties;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField pwdField;
    @FXML
    private Button togglePwdBtn;
    private final AuthService auth = AuthService.getInstance();
    private boolean pwdVisible = false;
    private TextField pwdVisibleField = new TextField();
    private String firebaseUrl;

    @FXML
    private void initialize() {
        try (InputStream in = getClass().getResourceAsStream("/config.properties")) {
            Properties props = new Properties();
            props.load(in);
            firebaseUrl = props.getProperty("firebase.databaseURL");
        } catch (Exception e) {
            firebaseUrl = "https://YOUR_PROJECT_ID.firebaseio.com/";
        }
        // Always clear login fields for security
        if (emailField != null)
            emailField.clear();
        if (pwdField != null)
            pwdField.clear();
        if (pwdVisibleField != null)
            pwdVisibleField.clear();
        pwdVisible = false;
        if (togglePwdBtn != null)
            togglePwdBtn.setText("👁️");
    }

    @FXML
    private void onLogin() {
        String email = emailField.getText().trim();
        String pwd = pwdField.getText();
        if (email.isEmpty() || pwd.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Email and password are required.");
            return;
        }
        auth.signIn(email, pwd).whenComplete((user, ex) -> {
            javafx.application.Platform.runLater(() -> {
                if (ex != null) {
                    showAlert(Alert.AlertType.ERROR, "Sign in failed: " + ex.getMessage());
                } else {
                    // Fetch user role from DB
                    String userId = email.replace("@", "_").replace(".", "_");
                    User u = new FirebaseUserDao(firebaseUrl).fetchById(userId);
                    NavigationManager.clearHistory();
                    if (u != null && "admin".equalsIgnoreCase(u.getRole())) {
                        NavigationManager.navigateTo("/com/arektaecomm/fxml/admin_dashboard.fxml");
                    } else {
                        NavigationManager.navigateTo("/com/arektaecomm/fxml/home.fxml");
                    }
                }
            });
        });
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.initOwner(emailField.getScene().getWindow());
        alert.showAndWait();
    }

    @FXML
    private void goToSignup() {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/arektaecomm/fxml/signup.fxml"));
            stage.getScene().setRoot(loader.load());
        } catch (Exception e) {

        }
    }

    @FXML
    private void onResetPassword() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please enter your email to reset password.");
            return;
        }
        auth.resetPassword(email).whenComplete((v, ex) -> {
            javafx.application.Platform.runLater(() -> {
                if (ex != null) {
                    showAlert(Alert.AlertType.ERROR, "Failed to send reset email: " + ex.getMessage());
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Password reset email sent. Please check your inbox.");
                }
            });
        });
    }

    @FXML
    private void onBack() {
        NavigationManager.goBack();
    }

    @FXML
    private void onCheckout() {
        com.arektaecomm.util.NavigationManager.navigateTo("/com/arektaecomm/fxml/target.fxml");
    }

    @FXML
    private void togglePasswordVisibility() {
        if (!pwdVisible) {
            pwdVisibleField.setText(pwdField.getText());
            pwdVisibleField.setPromptText("Password");
            pwdVisibleField.setMaxWidth(320);
            pwdVisibleField.setStyle(
                    "-fx-background-radius: 10; -fx-background-color: #f7f8fa; -fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-padding: 8 12; -fx-font-size: 15px;");
            ((HBox) pwdField.getParent()).getChildren().set(0, pwdVisibleField);
            togglePwdBtn.setText("🙈");
            pwdVisibleField.textProperty().bindBidirectional(pwdField.textProperty());
        } else {
            ((HBox) pwdVisibleField.getParent()).getChildren().set(0, pwdField);
            togglePwdBtn.setText("👁️");
        }
        pwdVisible = !pwdVisible;
    }

    public static void logout() {
        AuthService.getInstance().signOut();
        javafx.application.Platform.runLater(() -> {
            NavigationManager.clearHistory();
            NavigationManager.clearCache();
            NavigationManager.navigateTo("/com/arektaecomm/fxml/login.fxml");
        });
    }
}
