package com.arektaecomm.dao;

import com.arektaecomm.model.Product;
import com.arektaecomm.service.AuthService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class FirebaseProductDao implements ProductDao {
    private final String databaseUrl;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    public FirebaseProductDao(String databaseUrl) {
        this.databaseUrl = (databaseUrl != null && !databaseUrl.isEmpty())
                ? (databaseUrl.endsWith("/") ? databaseUrl : databaseUrl + "/")
                : "https://YOUR_PROJECT_ID.firebaseio.com/";
    }

    private String getAuthParam() {
        String idToken = AuthService.getInstance().getIdToken();
        return (idToken != null && !idToken.isEmpty()) ? "?auth=" + idToken : "";
    }

    @Override
    public void addProduct(Product p) {
        try {
            String key = UUID.randomUUID().toString();
            p.setId(key);
            String json = mapper.writeValueAsString(p);
            String url = databaseUrl + "products/" + key + ".json" + getAuthParam();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();
            client.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to add product: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateProduct(Product p) {
        if (p.getId() == null)
            throw new IllegalArgumentException("Product ID is null");
        try {
            String json = mapper.writeValueAsString(p);
            String url = databaseUrl + "products/" + p.getId() + ".json" + getAuthParam();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();
            client.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to update product: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteProduct(String productId) {
        try {
            String url = databaseUrl + "products/" + productId + ".json" + getAuthParam();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .DELETE()
                    .build();
            client.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete product: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Product> fetchAll() {
        List<Product> products = new ArrayList<>();
        try {
            String url = databaseUrl + "products.json" + getAuthParam();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                System.err.println("fetchAll products HTTP " + resp.statusCode() + ": " + resp.body());
            }
            if (resp.statusCode() == 200 && resp.body() != null && !resp.body().equals("null")) {
                JsonNode node = mapper.readTree(resp.body());
                node.fields().forEachRemaining(entry -> {
                    try {
                        Product p = mapper.treeToValue(entry.getValue(), Product.class);
                        products.add(p);
                    } catch (Exception e) {
                    }
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch products: " + e.getMessage(), e);
        }
        return products;
    }
}