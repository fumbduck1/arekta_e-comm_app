package com.arektaecomm.controller;

import com.arektaecomm.service.AuthService;
import com.arektaecomm.util.NavigationManager;
import com.arektaecomm.dao.FirebaseUserDao;
import com.arektaecomm.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class ProfileController {
    @FXML
    private Label emailLabel;
    @FXML
    private ImageView profileImageView;
    @FXML
    private TextField imgUrlField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField billingAddressField;
    @FXML
    private TextField mailingAddressField;

    private final AuthService authService = AuthService.getInstance();
    private final FirebaseUserDao userDao = new FirebaseUserDao(
            "https://arektaecomm1-default-rtdb.asia-southeast1.firebasedatabase.app/");

    @FXML
    public void initialize() {
        String email = authService.getCurrentUserEmail();
        emailLabel.setText("Email: " + (email != null ? email : "Not logged in"));
    // Make avatar circular using a clip
    javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(70, 70, 70);
    profileImageView.setClip(clip);
        loadProfileImage();
    }

    private void loadProfileImage() {
        String email = authService.getCurrentUserEmail();
        if (email == null)
            return;
        String userId = email.replace("@", "_").replace(".", "_");
        User user = userDao.fetchById(userId);
        if (user != null) {
            if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                profileImageView.setImage(new Image(user.getProfileImageUrl(), true));
                imgUrlField.setText(user.getProfileImageUrl());
            } else {
                profileImageView.setImage(new Image("https://ui-avatars.com/api/?name=User&background=eee&color=555"));
                imgUrlField.setText("");
            }
            nameField.setText(user.getName());
            phoneField.setText(user.getPhone());
            billingAddressField.setText(user.getBillingAddress());
            mailingAddressField.setText(user.getMailingAddress());
        } else {
            // Defaults if user record doesn't exist yet
            profileImageView.setImage(new Image("https://ui-avatars.com/api/?name=User&background=eee&color=555"));
            imgUrlField.setText("");
            nameField.setText("");
            phoneField.setText("");
            billingAddressField.setText("");
            mailingAddressField.setText("");
        }
    }

    @FXML
    private void onUpdateImage() {
        String url = imgUrlField.getText();
        if (url != null && !url.isBlank()) {
            saveProfileImageUrl(url);
            profileImageView.setImage(new Image(url, true));
        } else {
            showInfo("Please enter a valid image URL.");
        }
    }

    private void saveProfileImageUrl(String url) {
        String email = authService.getCurrentUserEmail();
        if (email == null)
            return;
        String userId = email.replace("@", "_").replace(".", "_");
        User user = getOrCreateUser(userId, email);
        if (user != null) {
            user.setProfileImageUrl(url);
            userDao.updateUser(user);
        }
    }

    @FXML
    private void onUpdateProfile() {
        String email = authService.getCurrentUserEmail();
        if (email == null)
            return;
        String userId = email.replace("@", "_").replace(".", "_");
    User user = getOrCreateUser(userId, email);
    user.setName(nameField.getText());
    user.setPhone(phoneField.getText());
    user.setBillingAddress(billingAddressField.getText());
    user.setMailingAddress(mailingAddressField.getText());
    userDao.updateUser(user);
    showInfo("Profile updated.");
    }

    @FXML
    private void onLogout() {
        authService.signOut();
    NavigationManager.clearHistory();
    NavigationManager.clearCache();
    NavigationManager.navigateTo("/com/arektaecomm/fxml/login.fxml");
    }

    @FXML
    private void onBack() {
        NavigationManager.goBack();
    }

    private void showInfo(String msg) {
        try {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION, msg, javafx.scene.control.ButtonType.OK);
            alert.setHeaderText(null);
            alert.setTitle("Profile");
            alert.showAndWait();
        } catch (Exception ignored) {}
    }

    private User getOrCreateUser(String userId, String email) {
        User user = userDao.fetchById(userId);
        if (user == null) {
            user = new User(userId, email, "user", "", "", "", "");
            userDao.updateUser(user);
        }
        return user;
    }

    @FXML
    private void onCropImage() {
        try {
            javafx.stage.Window win = profileImageView.getScene().getWindow();
            javafx.scene.image.Image src = profileImageView.getImage();
            if (src == null) {
                showInfo("Load an image first (paste URL and Update Image).");
                return;
            }
            javafx.scene.image.WritableImage cropped = com.arektaecomm.util.ImageCropper.cropInteractive(win, src, 280);
            if (cropped != null) {
                profileImageView.setImage(cropped);
                // Persist reference: since we avoid Swing conversion here, store the prior URL field
                // If the field is empty, keep the in-memory image without changing URL
                String currentUrl = imgUrlField.getText();
                if (currentUrl != null && !currentUrl.isBlank()) {
                    saveProfileImageUrl(currentUrl);
                }
            }
        } catch (Exception e) {
            System.err.println("Crop failed: " + e.getMessage());
        }
    }
}
