package com.arektaecomm.controller;

import com.arektaecomm.model.CartItem;
import com.arektaecomm.model.Product;
import com.arektaecomm.service.CartService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;

public class CartController {
    @FXML
    private ListView<CartItem> cartList;
    @FXML
    private Label totalLabel;
    @FXML
    private Label emptyCartLabel;
    @FXML
    private Button checkoutBtn;
    private static final CartService cartService = CartService.getInstance();

    @FXML
    public void initialize() {
        cartList.setItems(cartService.getItems());
        cartList.setCellFactory(list -> new CustomCartCell());
        updateTotal();
        updateEmptyState();
        cartService.getItems().addListener((javafx.collections.ListChangeListener.Change<? extends CartItem> c) -> {
            updateTotal();
            updateEmptyState();
        });
    }

    // Define your custom cell as an inner class or separate class
    private class CustomCartCell extends ListCell<CartItem> {
        private final ImageView imgView = new ImageView();
        private final Label nameLabel = new Label();
        private final Label priceLabel = new Label();
        private final Label subtotalLabel = new Label();
        private final Button minusBtn = new Button("-");
        private final Label qtyLabel = new Label();
        private final Button plusBtn = new Button("+");
        private final Button removeBtn = new Button("✖");
        private final HBox qtyBox = new HBox(4, minusBtn, qtyLabel, plusBtn);
        private final VBox infoBox = new VBox(2, nameLabel, priceLabel, subtotalLabel);
        private final HBox root = new HBox(16);

        {
            imgView.setFitWidth(48);
            imgView.setFitHeight(48);
            imgView.getStyleClass().add("cart-item-img");
            nameLabel.getStyleClass().add("cart-item-name");
            priceLabel.getStyleClass().add("cart-item-price");
            subtotalLabel.getStyleClass().add("cart-item-subtotal");
            minusBtn.getStyleClass().add("cart-item-qty-btn");
            plusBtn.getStyleClass().add("cart-item-qty-btn");
            removeBtn.getStyleClass().add("cart-item-remove-btn");
            qtyLabel.setMinWidth(24);
            qtyLabel.setAlignment(javafx.geometry.Pos.CENTER);

            qtyBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            root.getChildren().addAll(imgView, infoBox, qtyBox, removeBtn);
            root.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            minusBtn.setOnAction(e -> {
                CartItem item = getItem();
                if (item != null && item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                    cartList.refresh();
                    updateTotal();
                }
            });
            plusBtn.setOnAction(e -> {
                CartItem item = getItem();
                if (item != null) {
                    item.setQuantity(item.getQuantity() + 1);
                    cartList.refresh();
                    updateTotal();
                }
            });
            removeBtn.setOnAction(e -> {
                CartItem item = getItem();
                if (item != null)
                    cartService.getItems().remove(item);
            });
        }

        @Override
        protected void updateItem(CartItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                Product p = cartService.getProductMap().get(item.getProductId());
                if (p != null && p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
                    imgView.setImage(new Image(p.getImageUrl(), true));
                } else {
                    imgView.setImage(null);
                }
                nameLabel.setText(p != null ? p.getName() : item.getProductId());
                priceLabel.setText("৳" + (p != null ? String.format("%.2f", p.getPrice()) : "N/A"));
                qtyLabel.setText(String.valueOf(item.getQuantity()));
                double subtotal = (p != null ? p.getPrice() : 0) * item.getQuantity();
                subtotalLabel.setText("Subtotal: ৳" + String.format("%.2f", subtotal));
                setGraphic(root);
            }
        }
    }

    private void updateTotal() {
        totalLabel.setText(String.format("Total: ৳%.2f", cartService.getTotal()));
    }

    private void updateEmptyState() {
        boolean empty = cartService.getItems().isEmpty();
        if (emptyCartLabel != null)
            emptyCartLabel.setVisible(empty);
        if (checkoutBtn != null)
            checkoutBtn.setDisable(empty);
    }

    @FXML
    private void onBack() {
        com.arektaecomm.util.NavigationManager.goBack();
    }

    @FXML
    private void onCheckout() {
        com.arektaecomm.util.NavigationManager.navigateTo("/com/arektaecomm/fxml/checkout.fxml");
    }

    @FXML
    private void onHome() {
        com.arektaecomm.util.NavigationManager.navigateTo("/com/arektaecomm/fxml/home.fxml");
    }

    @FXML
    private void onCart() {
        // Do nothing or show a message: already here
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
    private void onDisableCartButton() {
        checkoutBtn.setDisable(true);
    }
}