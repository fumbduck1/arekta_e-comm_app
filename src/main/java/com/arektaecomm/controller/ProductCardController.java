package com.arektaecomm.controller;

import com.arektaecomm.model.CartItem;
import com.arektaecomm.model.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import com.arektaecomm.service.CartService;

public class ProductCardController {
    @FXML
    private ImageView imgView;
    @FXML
    private Label nameLabel, priceLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private VBox rootCard; // Add fx:id="rootCard" to the VBox in product_card.fxml
    private Product product;
    private static final CartService cartService = CartService.getInstance();

    public void setProduct(Product p) {
        this.product = p;
        nameLabel.setText(p.getName());
        priceLabel.setText(String.format("৳%.2f", p.getPrice()));
        try {
            if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
                imgView.setImage(new Image(p.getImageUrl(), true));
            } else {
                imgView.setImage(null);
            }
        } catch (Exception e) {
            imgView.setImage(null);
        }
        cartService.addProductInfo(p);
        if (statusLabel != null) {
            statusLabel.setVisible(false);
            statusLabel.setText("");
        }
        rootCard.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                com.arektaecomm.util.NavigationManager.navigateToWithData("/com/arektaecomm/fxml/product_details.fxml",
                        p);
            }
        });
    }

    @FXML
    private void onAddToCart() {
        cartService.addItem(new CartItem(product.getId(), 1));
        if (statusLabel != null) {
            statusLabel.setText("Added to cart!");
            statusLabel.setVisible(true);
            // Hide after 1.5 seconds
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
                    javafx.util.Duration.seconds(1.5));
            pause.setOnFinished(e -> statusLabel.setVisible(false));
            pause.play();
        }
    }

    @FXML
    private void onBack() {
        com.arektaecomm.util.NavigationManager.goBack();
    }

    @FXML
    private void onCheckout() {
        com.arektaecomm.util.NavigationManager.navigateTo("/com/arektaecomm/fxml/target.fxml");
    }

    @FXML
    private void onHome() {
        com.arektaecomm.util.NavigationManager.navigateTo("/com/arektaecomm/fxml/home.fxml");
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
    private void onProductCard() {
        // Do nothing or show a message: already here
    }
}