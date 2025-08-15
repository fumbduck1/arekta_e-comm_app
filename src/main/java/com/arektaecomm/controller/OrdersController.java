package com.arektaecomm.controller;

import com.arektaecomm.dao.FirebaseOrderDao;
import com.arektaecomm.model.Order;
import com.arektaecomm.service.AuthService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public class OrdersController {
    @FXML
    private ListView<String> ordersList;
    @FXML
    private Label emptyOrdersLabel;
    @FXML
    private Button backBtn;
    private final ObservableList<String> orderSummaries = FXCollections.observableArrayList();
    private final FirebaseOrderDao orderDao;

    private static Consumer<Void> orderPlacedListener;

    public OrdersController() {
        String dbUrl = null;
        try (InputStream in = getClass().getResourceAsStream("/config.properties")) {
            Properties props = new Properties();
            props.load(in);
            dbUrl = props.getProperty("firebase.databaseURL");
        } catch (Exception e) {
            dbUrl = "https://YOUR_PROJECT_ID.firebaseio.com/";
        }
        orderDao = new FirebaseOrderDao(dbUrl);
    }

    public static void setOrderPlacedListener(Consumer<Void> listener) {
        orderPlacedListener = listener;
    }

    public static void notifyOrderPlaced() {
        if (orderPlacedListener != null)
            orderPlacedListener.accept(null);
    }

    @FXML
    public void initialize() {
        refreshOrders();
        setOrderPlacedListener(v -> refreshOrders());
    }

    public void refreshOrders() {
        orderSummaries.clear();
        String email = AuthService.getInstance().getCurrentUserEmail();
        String userId = email != null ? email.replace("@", "_").replace(".", "_") : null;
        List<Order> orders = (userId != null) ? orderDao.fetchByUser(userId) : List.of();
        if (orders.isEmpty()) {
            emptyOrdersLabel.setVisible(true);
        } else {
            emptyOrdersLabel.setVisible(false);
            for (Order o : orders) {
                orderSummaries
                        .add("Order #" + o.getId() + " | Total: ৳" + o.getTotal() + " | Status: " + o.getStatus());
            }
        }
        ordersList.setItems(orderSummaries);
    }

    @FXML
    public void onBack() {
        com.arektaecomm.util.NavigationManager.goBack();
    }
}
