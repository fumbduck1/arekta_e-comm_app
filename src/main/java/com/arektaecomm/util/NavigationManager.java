package com.arektaecomm.util;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;

public class NavigationManager {
    private static final Stack<String> history = new Stack<>();
    private static final Map<String, Scene> sceneCache = new HashMap<>();
    private static Stage primaryStage;
    private static String currentFxml = null;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void navigateTo(String fxmlPath) {
        if (primaryStage == null)
            return;
        if (currentFxml != null)
            history.push(currentFxml);
        try {
            Scene scene;
        // Screens that must always be fresh (no cache) so controllers re-init
        boolean noCache = fxmlPath.contains("product_details.fxml")
            || fxmlPath.contains("login.fxml")
            || fxmlPath.contains("signup.fxml")
            || fxmlPath.contains("home.fxml");
        if (noCache) {
                FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(fxmlPath));
                scene = new Scene(loader.load());
                attachStyles(scene);
            } else {
                scene = sceneCache.get(fxmlPath);
                if (scene == null) {
                    FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(fxmlPath));
                    scene = new Scene(loader.load());
                    attachStyles(scene);
                    sceneCache.put(fxmlPath, scene);
                }
            }
            primaryStage.setScene(scene);
            currentFxml = fxmlPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void goBack() {
        if (primaryStage == null || history.isEmpty())
            return;
        String prevFxml = history.pop();
        try {
            Scene scene = sceneCache.get(prevFxml);
            if (scene == null) {
                FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(prevFxml));
                scene = new Scene(loader.load());
                attachStyles(scene);
                sceneCache.put(prevFxml, scene);
            }
            primaryStage.setScene(scene);
            currentFxml = prevFxml;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearHistory() {
        history.clear();
    }

    public static void clearCache() {
        sceneCache.clear();
        currentFxml = null;
    }

    private static Object navigationData = null;

    public static void navigateToWithData(String fxmlPath, Object data) {
        navigationData = data;
        navigateTo(fxmlPath);
    }

    public static Object getNavigationData() {
        Object data = navigationData;
        navigationData = null; // Clear after retrieval for safety
        return data;
    }

    private static void attachStyles(Scene scene) {
        try {
            String css = NavigationManager.class
                    .getResource("/com/arektaecomm/css/styles.css")
                    .toExternalForm();
            if (!scene.getStylesheets().contains(css)) {
                scene.getStylesheets().add(css);
            }
        } catch (Exception ignore) {
            // If stylesheet not found, ignore; app can still run with Modena
        }
    }
}