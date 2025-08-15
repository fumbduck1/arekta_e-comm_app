package com.arektaecomm.service;

import com.arektaecomm.model.CartItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.arektaecomm.model.Product;
import java.util.HashMap;

public class CartService {
    private final ObservableList<CartItem> items = FXCollections.observableArrayList();
    private final HashMap<String, Product> productMap = new HashMap<>(); // productId -> Product

    public HashMap<String, Product> getProductMap() {
        return productMap;
    }

    public ObservableList<CartItem> getItems() {
        return items;
    }

    public void addProductInfo(Product p) {
        productMap.put(p.getId(), p);
    }

    public void addItem(CartItem item) {
        for (CartItem ci : items) {
            if (ci.getProductId().equals(item.getProductId())) {
                ci.setQuantity(ci.getQuantity() + item.getQuantity());
                return;
            }
        }
        items.add(item);
    }

    public double getTotal() {
        return items.stream()
                .mapToDouble(i -> {
                    Product p = productMap.get(i.getProductId());
                    return (p != null ? p.getPrice() : 0) * i.getQuantity();
                })
                .sum();
    }

    public void clear() {
        items.clear();
    }

    private static final CartService INSTANCE = new CartService();

    private CartService() {
    }

    public static CartService getInstance() {
        return INSTANCE;
    }
}