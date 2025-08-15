package com.arektaecomm.controller;

import com.arektaecomm.model.*;
import com.arektaecomm.model.Order;
import com.arektaecomm.service.CartService;
import com.arektaecomm.service.OrderService;
import com.arektaecomm.service.AuthService;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Properties;
import java.io.InputStream;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class CheckoutController {
    @FXML
    private ToggleGroup paymentGroup;
    @FXML
    private RadioButton ccOption, ppOption, codOption;
    @FXML
    private Label paymentWarningLabel;
    @FXML
    private Button payBtn;

    private static final CartService cartService = CartService.getInstance();
    private final OrderService orderService;

    public CheckoutController() {
        String dbUrl = null;
        try (InputStream in = getClass().getResourceAsStream("/config.properties")) {
            Properties props = new Properties();
            props.load(in);
            dbUrl = props.getProperty("firebase.databaseURL");
        } catch (Exception e) {
            e.printStackTrace();
            dbUrl = "https://YOUR_PROJECT_ID.firebaseio.com/";
        }
        orderService = new OrderService(dbUrl);
    }

    @FXML
    private void onPay() {
        if (!ccOption.isSelected() && !ppOption.isSelected() && !codOption.isSelected()) {
            if (paymentWarningLabel != null)
                paymentWarningLabel.setVisible(true);
            return;
        }
        if (paymentWarningLabel != null)
            paymentWarningLabel.setVisible(false);
        if (payBtn != null)
            payBtn.setDisable(true);

        PaymentMethod method;
        if (ccOption.isSelected())
            method = new CreditCardPayment();
        else if (ppOption.isSelected())
            method = new PaypalPayment();
        else
            method = new CashOnDelivery();

        Order order = new Order();
        order.setItems(cartService.getItems());
        order.setTotal(cartService.getTotal());
        order.setStatus("Placed");
        String email = AuthService.getInstance().getCurrentUserEmail();
        if (email == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "You must be logged in to place an order.", ButtonType.OK);
            alert.showAndWait();
            if (payBtn != null)
                payBtn.setDisable(false);
            return;
        }
        order.setUserId(email.replace("@", "_").replace(".", "_"));
        System.out.println("Order placed: userId=" + order.getUserId());

        try {
            method.pay(order);
            orderService.placeOrder(order, email);
            cartService.clear();
            com.arektaecomm.controller.OrdersController.notifyOrderPlaced();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Order placed successfully!", ButtonType.OK);
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Order failed: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        } finally {
            if (payBtn != null)
                payBtn.setDisable(false);
        }
    }

    @FXML
    private void onBack() {
        com.arektaecomm.util.NavigationManager.goBack();
    }

    @FXML
    private void onCheckout() {
        // Do nothing or show a message: already here
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

}