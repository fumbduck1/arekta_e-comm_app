
package com.arektaecomm.dao;

import com.arektaecomm.model.Order;
import com.arektaecomm.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class FirebaseOrderDao implements OrderDao {
    private final String databaseUrl;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    public FirebaseOrderDao(String databaseUrl) {
        this.databaseUrl = databaseUrl.endsWith("/") ? databaseUrl : databaseUrl + "/";
    }

    private String getAuthParam() {
        String idToken = AuthService.getInstance().getIdToken();
        return (idToken != null && !idToken.isEmpty()) ? "?auth=" + idToken : "";
    }

    @Override
    public void createOrder(Order o) {
        try {
            String key = UUID.randomUUID().toString();
            o.setId(key);
            String json = mapper.writeValueAsString(o);
            String url = databaseUrl + "orders/" + key + ".json" + getAuthParam();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println("Firebase createOrder response code: " + resp.statusCode());
            System.out.println("Firebase createOrder response: " + resp.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateOrder(Order o) {
        // Overwrite by id
        try {
            String json = mapper.writeValueAsString(o);
            String url = databaseUrl + "orders/" + o.getId() + ".json" + getAuthParam();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println("Firebase updateOrder response code: " + resp.statusCode());
            System.out.println("Firebase updateOrder response: " + resp.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Order> fetchByUser(String userId) {
        List<Order> orders = new ArrayList<>();
        try {
            String url = databaseUrl + "orders.json" + getAuthParam();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200 && resp.body() != null && !resp.body().equals("null")) {
                var node = mapper.readTree(resp.body());
                node.fields().forEachRemaining(entry -> {
                    try {
                        Order o = mapper.treeToValue(entry.getValue(), Order.class);
                        if (o != null && userId.equals(o.getUserId())) {
                            orders.add(o);
                        }
                    } catch (Exception ignored) {
                    }
                });
            }
        } catch (Exception e) {
        }
        return orders;
    }

    @Override
    // Fetch all orders (for admin)
    public List<Order> fetchAll() {
        List<Order> orders = new ArrayList<>();
        try {
            String url = databaseUrl + "orders.json" + getAuthParam();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200 && resp.body() != null && !resp.body().equals("null")) {
                var node = mapper.readTree(resp.body());
                node.fields().forEachRemaining(entry -> {
                    try {
                        Order o = mapper.treeToValue(entry.getValue(), Order.class);
                        if (o != null)
                            orders.add(o);
                    } catch (Exception ignored) {
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }
}