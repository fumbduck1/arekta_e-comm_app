package com.arektaecomm.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.arektaecomm.dao.FirebaseOrderDao;
import com.arektaecomm.model.Product;
import com.arektaecomm.model.Order;
import com.arektaecomm.service.ProductService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Properties;
import java.io.InputStream;

public class AdminDashboardController {
    @FXML
    private TextField orderSearchField;
    private String lastOrderSearch = null;
    // Product management
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, String> idCol, nameCol;
    @FXML
    private TableColumn<Product, Number> priceCol, stockCol;
    @FXML
    private Label emptyProductLabel;
    @FXML
    private TextField nameField, priceField, stockField;
    @FXML
    private Button addUpdateBtn, deleteBtn;
    private final String firebaseUrl;
    private final ProductService productService;
    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private Product selected = null;
    @FXML
    private Label productActionStatus;
    @FXML
    private TextField imageUrlField, descField;

    // Campaign/carousel management
    @FXML
    private TextField carouselImgUrlField;
    @FXML
    private Button addImageBtn;
    @FXML
    private Button removeImageBtn;
    @FXML
    private ListView<String> carouselList;
    @FXML
    private Label uploadStatus;
    private final ObservableList<String> carouselImages = FXCollections.observableArrayList();
    @FXML
    private Label emptyCarouselLabel;

    // Order management
    @FXML
    private TableView<Order> orderTable;
    @FXML
    private TableColumn<Order, String> orderIdCol, orderUserCol, orderStatusCol;
    @FXML
    private TableColumn<Order, Number> orderTotalCol;
    @FXML
    private Label emptyOrderLabel;
    @FXML
    private Button markShippedBtn, markDeliveredBtn, refundBtn;
    @FXML
    private Label orderActionStatus;
    private final FirebaseOrderDao orderDao;
    private final ObservableList<Order> orders = FXCollections.observableArrayList();
    private Order selectedOrder = null;

    // Analytics (placeholder)
    @FXML
    private Label totalSalesLabel;
    @FXML
    private Label totalOrdersLabel;
    @FXML
    private Label topProductLabel;

    /**
     * Loads Firebase URL from config.properties and initializes DAOs/services.
     */
    public AdminDashboardController() {
        String url = null;
        try (InputStream in = getClass().getResourceAsStream("/config.properties")) {
            Properties props = new Properties();
            props.load(in);
            url = props.getProperty("firebase.databaseURL");
        } catch (Exception e) {
            e.printStackTrace();
            url = "https://YOUR_PROJECT_ID.firebaseio.com/";
        }
        this.firebaseUrl = url;
        this.productService = new ProductService(firebaseUrl);
        this.orderDao = new FirebaseOrderDao(firebaseUrl);

    }

    /**
     * Initializes the admin dashboard UI components and event handlers.
     */
    @FXML
    public void initialize() {
        // --- Order Table Setup ---
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        orderUserCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        orderTotalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        orderStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        orderTable.setItems(orders);
        refreshOrders();

        // Set selectedOrder when an order is selected
        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            selectedOrder = newSel;
        });

        // --- Product Table Setup ---
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productTable.setItems(products);
        refreshProducts();
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            selected = newSel;
            if (newSel != null) {
                nameField.setText(newSel.getName());
                priceField.setText(String.valueOf(newSel.getPrice()));
                stockField.setText(String.valueOf(newSel.getStock()));
                imageUrlField.setText(newSel.getImageUrl() != null ? newSel.getImageUrl() : "");
                descField.setText(newSel.getDescription() != null ? newSel.getDescription() : "");
            }
        });

    }

    private void refreshProducts() {
        products.setAll(productService.getAllProducts());
        updateEmptyProductLabel();
    }

    public void updateEmptyCarouselLabel() {
        if (emptyCarouselLabel != null)
            emptyCarouselLabel.setVisible(carouselImages.isEmpty());
    }

    private void updateEmptyOrderLabel() {
        if (emptyOrderLabel != null)
            emptyOrderLabel.setVisible(orders.isEmpty());
    }

    /**
     * Handles add or update of a product, with error handling and user feedback.
     */
    @FXML
    public void onAddOrUpdate() {
        String name = nameField != null ? nameField.getText() : null;
        String priceText = priceField != null ? priceField.getText() : null;
        String stockText = stockField != null ? stockField.getText() : null;
        String imageUrl = imageUrlField != null ? imageUrlField.getText() : null;
        String desc = descField != null ? descField.getText() : null;

        // Basic validation
        if (name == null || name.isBlank()) {
            notifyProductStatus("Name is required.");
            return;
        }
        if (priceText == null || priceText.isBlank()) {
            notifyProductStatus("Price is required.");
            return;
        }
        if (stockText == null || stockText.isBlank()) {
            notifyProductStatus("Stock is required.");
            return;
        }

        // Accept comma-formatted numbers like "105,500.00"
        double price;
        int stock;
        try {
            String priceSan = priceText.replace(",", "").trim();
            // Remove currency symbols/spaces if any
            priceSan = priceSan.replaceAll("[^0-9.\\-]", "");
            price = Double.parseDouble(priceSan);
        } catch (NumberFormatException ex) {
            notifyProductStatus("Invalid price. Use numbers like 105500.00");
            return;
        }
        try {
            String stockSan = stockText.replace(",", "").trim();
            stockSan = stockSan.replaceAll("[^0-9\\-]", "");
            stock = Integer.parseInt(stockSan);
        } catch (NumberFormatException ex) {
            notifyProductStatus("Invalid stock. Use whole numbers like 12");
            return;
        }
        if (price < 0) {
            notifyProductStatus("Price cannot be negative.");
            return;
        }
        if (stock < 0) {
            notifyProductStatus("Stock cannot be negative.");
            return;
        }

        try {
            Product p = (selected == null) ? new Product(null, name, desc, price, stock, imageUrl) : selected;
            p.setName(name);
            p.setPrice(price);
            p.setStock(stock);
            p.setImageUrl(imageUrl);
            p.setDescription(desc);
            productService.addOrUpdate(p);
            refreshProducts();
            clearForm();
            notifyProductStatus((selected == null) ? "Product added." : "Product updated.");
        } catch (Exception ex) {
            notifyProductStatus("Failed to add/update product: " + ex.getMessage());
        }
    }

    private void notifyProductStatus(String message) {
        if (productActionStatus != null) {
            productActionStatus.setText(message);
        } else {
            // Fallback if the status label is not present in FXML
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
            alert.setHeaderText(null);
            alert.setTitle("Product");
            alert.showAndWait();
        }
    }

    /**
     * Handles deletion of a selected product, with error handling and user
     * feedback.
     */
    @FXML
    public void onDelete() {
        if (selected != null) {
            try {
                productService.remove(selected.getId());
                refreshProducts();
                clearForm();
                if (productActionStatus != null)
                    productActionStatus.setText("Product deleted.");
            } catch (Exception ex) {
                if (productActionStatus != null)
                    productActionStatus.setText("Failed to delete product: " + ex.getMessage());
            }
        } else {
            if (productActionStatus != null)
                productActionStatus.setText("No product selected.");
        }
    }

    /**
     * Clears the product form fields and selection.
     */
    private void clearForm() {
        productTable.getSelectionModel().clearSelection();
        nameField.clear();
        priceField.clear();
        stockField.clear();
        imageUrlField.clear();
        descField.clear();
        selected = null;
    }

    /**
     * Loads carousel images (simulated or from Firebase in the future).
     * 
     * 
     * /**
     * Handles image selection and (future) upload for campaign/carousel.
     */

    /**
     * Refreshes the order list from the database.
     */
    private void refreshOrders() {
        // Preserve selected order ID
        String selectedId = null;
        Order selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedId = selected.getId();
        }
        var allOrders = orderDao.fetchAll();
        if (lastOrderSearch != null && !lastOrderSearch.isBlank()) {
            orders.setAll(allOrders.stream()
                    .filter(o -> o.getId() != null && o.getId().toLowerCase().contains(lastOrderSearch.toLowerCase()))
                    .toList());
        } else {
            orders.setAll(allOrders);
        }
        // Restore selection if possible
        if (selectedId != null) {
            for (Order o : orders) {
                if (selectedId.equals(o.getId())) {
                    orderTable.getSelectionModel().select(o);
                    break;
                }
            }
        }
        updateEmptyOrderLabel();
        updateAnalytics();
    }

    @FXML
    public void onOrderSearch() {
        if (orderSearchField != null) {
            lastOrderSearch = orderSearchField.getText();
            refreshOrders();
        }
    }

    @FXML
    public void onOrderSearchClear() {
        if (orderSearchField != null) {
            orderSearchField.clear();
            lastOrderSearch = null;
            refreshOrders();
        }
    }

    /**
     * Updates the status of the selected order, with confirmation and error
     * handling.
     */
    private void updateOrderStatus(String status) {
        if (selectedOrder != null) {
            try {
                selectedOrder.setStatus(status);
                orderDao.updateOrder(selectedOrder);
                refreshOrders();
                orderActionStatus.setText("Order " + selectedOrder.getId() + " marked as " + status);
            } catch (Exception ex) {
                orderActionStatus.setText("Failed to update order: " + ex.getMessage());
            }
        } else {
            orderActionStatus.setText("No order selected.");
        }
    }

    /**
     * Shows a confirmation dialog before updating order status.
     */
    public void confirmAndUpdateOrderStatus(String status) {
        if (selectedOrder == null) {
            orderActionStatus.setText("No order selected.");
            return;
        }
        String msg = "Are you sure you want to mark order '" + selectedOrder.getId() + "' as '" + status + "'?";
        if (status.equals("Refunded")) {
            msg = "Are you sure you want to REFUND order '" + selectedOrder.getId() + "'? This cannot be undone.";
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Confirm Action");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                updateOrderStatus(status);
            }
        });
    }

    /**
     * Updates analytics labels (placeholder for real analytics logic).
     */
    private void updateAnalytics() {
        // TODO: real analytics calculations
        double totalSales = orders.stream().mapToDouble(Order::getTotal).sum();
        int totalOrders = orders.size();
        String topProduct = "N/A";
        // Optionally, calculate top product by sales
        // ...
        if (totalSalesLabel != null)
            totalSalesLabel.setText("Total Sales: ৳" + String.format("%.2f", totalSales));
        if (totalOrdersLabel != null)
            totalOrdersLabel.setText("Total Orders: " + totalOrders);
        if (topProductLabel != null)
            topProductLabel.setText("Top Product: " + topProduct);
    }

    /**
     * Updates the empty product label visibility based on the products list.
     */
    private void updateEmptyProductLabel() {
        if (emptyProductLabel != null)
            emptyProductLabel.setVisible(products.isEmpty());
    }

    @FXML
    private void onBack() {
    com.arektaecomm.util.NavigationManager.goBack();
    updateEmptyCarouselLabel();
    }

    @FXML
    private void onLogout() {
        LoginController.logout();
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
        // Do nothing or show a message: already here
    }

    @FXML
    public void onMarkShipped() {
        Order selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedOrder = selected;
            confirmAndUpdateOrderStatus("Shipped");
        } else {
            orderActionStatus.setText("No order selected.");
        }
    }

    @FXML
    public void onMarkDelivered() {
        Order selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedOrder = selected;
            confirmAndUpdateOrderStatus("Delivered");
        } else {
            orderActionStatus.setText("No order selected.");
        }
    }

    @FXML
    public void onRefund() {
        Order selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedOrder = selected;
            confirmAndUpdateOrderStatus("Refunded");
        } else {
            orderActionStatus.setText("No order selected.");
        }
    }
}