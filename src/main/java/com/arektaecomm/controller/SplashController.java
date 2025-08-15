package com.arektaecomm.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class SplashController {
    @FXML
    private ImageView splashImage;
    @FXML
    private Label projectNameLabel;
    @FXML
    private Label loadingLabel;

    @FXML
    public void initialize() {
        splashImage.setImage(new Image(getClass().getResourceAsStream("/com/arektaecomm/images/splash_screen.png")));

        // Typing effect for project name
        String projectName = "Arekta E-Commerce App";
        Timeline typing = new Timeline();
        for (int i = 0; i <= projectName.length(); i++) {
            final int idx = i;
            typing.getKeyFrames().add(
                    new KeyFrame(Duration.millis(80 * i),
                            e -> projectNameLabel.setText(projectName.substring(0, idx))));
        }
        typing.play();

        // Optional: Animate loading label (fade or dots)
        if (loadingLabel != null) {
            Timeline dots = new Timeline(
                    new KeyFrame(Duration.ZERO, e -> loadingLabel.setText("Loading.")),
                    new KeyFrame(Duration.seconds(0.5), e -> loadingLabel.setText("Loading..")),
                    new KeyFrame(Duration.seconds(1), e -> loadingLabel.setText("Loading...")));
            dots.setCycleCount(Timeline.INDEFINITE);
            dots.play();
        }
    }

    @FXML
    private void onBack() {
        com.arektaecomm.util.NavigationManager.goBack();
    }

    @FXML
    private void onCheckout() {
        com.arektaecomm.util.NavigationManager.navigateTo("/com/arektaecomm/fxml/checkout.fxml");
    }
}