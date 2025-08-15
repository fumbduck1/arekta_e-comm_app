package com.arektaecomm.controller;

import com.arektaecomm.model.Product;
import com.arektaecomm.model.CartItem;
import com.arektaecomm.service.CartService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class ProductDetailsController {
    private Product product;

    @FXML
    private ImageView imgView;
    @FXML
    private Label nameLabel, priceLabel, stockLabel, descLabel;

    @FXML
    public void initialize() {
        // Try to get product from NavigationManager data
        Object navData = com.arektaecomm.util.NavigationManager.getNavigationData();
        if (navData instanceof Product) {
            setProduct((Product) navData);
            product = (Product) navData;
        }
    }

    public void setProduct(Product p) {
        nameLabel.setText(p.getName());
        priceLabel.setText(String.format("৳%.2f", p.getPrice()));
        stockLabel.setText("Stock: " + p.getStock());
        descLabel.setText(p.getDescription() != null ? p.getDescription() : "");
        if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
            imgView.setImage(new Image(p.getImageUrl(), true));
        } else {
            imgView.setImage(null);
        }
        this.product = p;
    }

    @FXML
    private void onAddToCart() {
        if (product != null) {
            CartService.getInstance().addItem(new CartItem(product.getId(), 1));
        }
    }

    @FXML
    private void onBack() {
        com.arektaecomm.util.NavigationManager.goBack();
    }
}
