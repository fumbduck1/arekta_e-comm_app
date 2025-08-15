package com.arektaecomm;
import com.arektaecomm.util.FirebaseInit;
import com.arektaecomm.util.NavigationManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FirebaseInit.initialize();
        NavigationManager.setStage(primaryStage);
        FXMLLoader splashLoader = new FXMLLoader(getClass().getResource("/com/arektaecomm/fxml/splash.fxml"));
        Scene splashScene = new Scene(splashLoader.load());
        try {
            String css = getClass().getResource("/com/arektaecomm/css/styles.css").toExternalForm();
            splashScene.getStylesheets().add(css);
        } catch (Exception ignore) {}
        primaryStage.setTitle("Arekta E-Comm App");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/arektaecomm/images/app_icon(64x64).png")));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/arektaecomm/images/app_icon(256x256).png")));
        primaryStage.setScene(splashScene);
        primaryStage.show();
        javafx.animation.PauseTransition splashDelay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
        splashDelay.setOnFinished(e -> {
            try {
                NavigationManager.navigateTo("/com/arektaecomm/fxml/login.fxml");
            } catch (Exception ex) {
            }
        });
        splashDelay.play();
    }
    public static void main(String[] args) {
        launch(args);
    }
}