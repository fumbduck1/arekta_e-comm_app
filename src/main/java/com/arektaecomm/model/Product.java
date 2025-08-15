package com.arektaecomm.model;

import com.arektaecomm.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Product {
    private String id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private String imageUrl;
    private static final String databaseUrl = "https://your-database-url/"; // Replace with your database URL
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    // Default constructor
    public Product() {
    }

    // Constructors, getters/setters
    public Product(String id, String name, String description, double price, int stock, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void addProduct(Product p) {
        try {
            String key = p.getId() != null ? p.getId() : UUID.randomUUID().toString();
            p.setId(key);
            String idToken = AuthService.getInstance().getIdToken();
            String url = databaseUrl + "products/" + key + ".json?auth=" + idToken;
            String json = mapper.writeValueAsString(p);
            System.out.println("DEBUG: PUT " + url + " BODY: " + json);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println("DEBUG: RESPONSE: " + resp.body());
        } catch (Exception e) {
            throw new RuntimeException("Failed to add product: " + e.getMessage(), e);
        }
    }

    public List<Product> fetchAll() {
        List<Product> products = new ArrayList<>();
        try {
            String idToken = AuthService.getInstance().getIdToken();
            String url = databaseUrl + "products.json?auth=" + idToken;
            System.out.println("DEBUG: GET " + url);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println("DEBUG: RESPONSE: " + resp.body());
            if (resp.statusCode() == 200 && resp.body() != null && !resp.body().equals("null")) {
                JsonNode node = mapper.readTree(resp.body());
                node.fields().forEachRemaining(entry -> {
                    try {
                        Product p = mapper.treeToValue(entry.getValue(), Product.class);
                        products.add(p);
                    } catch (Exception ignored) {
                    }
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch products: " + e.getMessage(), e);
        }
        return products;
    }
}