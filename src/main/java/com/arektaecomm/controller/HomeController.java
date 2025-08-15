package com.arektaecomm.controller;

import javafx.scene.layout.HBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import com.arektaecomm.model.Product;
import com.arektaecomm.service.ProductService;
import java.io.InputStream;
import java.util.Properties;

public class HomeController {
    @FXML
    private HBox carouselBox;

    // Removed unused VBox productList
    @FXML
    private FlowPane productGrid;
    @FXML
    private Label emptyLabel;

    // Removed unused Button addCarouselImageBtn
    private final ProductService productService;

    // Removed unused field carouselImages
    private static Runnable carouselUpdateListener;

    public HomeController() {
        String dbUrl = null;
        try (InputStream in = getClass().getResourceAsStream("/config.properties")) {
            Properties props = new Properties();
            props.load(in);
            dbUrl = props.getProperty("firebase.databaseURL");
        } catch (Exception e) {
            e.printStackTrace();
            dbUrl = "https://YOUR_PROJECT_ID.firebaseio.com/";
        }
        productService = new ProductService(dbUrl);

    }

    public static void setCarouselUpdateListener(Runnable listener) {
        carouselUpdateListener = listener;
    }

    public static void notifyCarouselUpdated() {
        if (carouselUpdateListener != null)
            carouselUpdateListener.run();
    }

    @FXML
    public void initialize() {

        loadCampaignSlider();
        refreshProducts();
    }

    private void loadCampaignSlider() {

        carouselBox.getChildren().clear();
        javafx.scene.image.ImageView img = new javafx.scene.image.ImageView(
                getClass().getResource("/com/arektaecomm/images/splash_screen.png").toExternalForm());
        img.setFitWidth(700);
        img.setFitHeight(220);
        img.setPreserveRatio(true);
        carouselBox.getChildren().add(img);
    }

    private void refreshProducts() {
        productGrid.getChildren().clear();
        try {
            var products = productService.getAllProducts();
            if (products.isEmpty()) {
                emptyLabel.setVisible(true);
            } else {
                emptyLabel.setVisible(false);
                for (Product p : products) {
                    try {
                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource("/com/arektaecomm/fxml/product_card.fxml"));
                        Node card = loader.load();
                        ProductCardController ctrl = loader.getController();
                        ctrl.setProduct(p);
                        productGrid.getChildren().add(card);
                    } catch (Exception ignored) {
                    }
                }
            }
        } catch (RuntimeException ex) {
            // Likely an auth (401/403) or connectivity issue; show empty state
            System.err.println("Failed to load products: " + ex.getMessage());
            emptyLabel.setVisible(true);
        }
    }

    @FXML
    private void onCart() {
        com.arektaecomm.util.NavigationManager.navigateTo("/com/arektaecomm/fxml/cart.fxml");
    }

    @FXML
    private void onOrders() {
        com.arektaecomm.util.NavigationManager.navigateTo("/com/arektaecomm/fxml/orders.fxml");
    }

    @FXML
    private void onProfile() {
        com.arektaecomm.util.NavigationManager.navigateTo("/com/arektaecomm/fxml/profile.fxml");
    }

    @FXML
    private void onAdmin() {
        com.arektaecomm.util.NavigationManager.navigateTo("/com/arektaecomm/fxml/admin_dashboard.fxml");
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