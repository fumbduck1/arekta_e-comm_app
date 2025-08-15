package com.arektaecomm.dao;

import com.arektaecomm.model.User;
import com.arektaecomm.service.AuthService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FirebaseUserDao implements UserDao {
    private final String databaseUrl;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    public FirebaseUserDao(String databaseUrl) {
        this.databaseUrl = databaseUrl.endsWith("/") ? databaseUrl : databaseUrl + "/";
    }

    @Override
    public User fetchById(String userId) {
        try {
            String idToken = AuthService.getInstance().getIdToken();
            String url = databaseUrl + "users/" + userId + ".json" + (idToken != null && !idToken.isEmpty() ? ("?auth=" + idToken) : "");
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200 && resp.body() != null && !resp.body().equals("null")) {
                JsonNode node = mapper.readTree(resp.body());
                User user = new User(
                        userId,
                        node.path("email").asText(),
                        node.path("role").asText("user"),
                        node.path("name").asText(""),
                        node.path("phone").asText(""),
                        node.path("billingAddress").asText(""),
                        node.path("mailingAddress").asText(""));
                user.setProfileImageUrl(node.path("profileImageUrl").asText(""));
                return user;
            } else if (resp.statusCode() == 200 && (resp.body() == null || resp.body().equals("null"))) {
                // No record yet; return a default object so callers can upsert
                return new User(userId, "", "user", "", "", "", "");
            }
        } catch (Exception e) {
        System.err.println("Failed to fetch user: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void updateUser(User u) {
        try {
            String json = mapper.writeValueAsString(u);
        String idToken = AuthService.getInstance().getIdToken();
        String url = databaseUrl + "users/" + u.getId() + ".json" + (idToken != null && !idToken.isEmpty() ? ("?auth=" + idToken) : "");
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(url))
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();
            client.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
        System.err.println("Failed to update user: " + e.getMessage());
        }
    }
}