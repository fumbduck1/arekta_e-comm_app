package com.arektaecomm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class AuthService {
    private static final AuthService INSTANCE = new AuthService();

    private final String apiKey;
    private final HttpClient client;
    private final ObjectMapper mapper;

    private String idToken;
    private String currentUserEmail;

    private AuthService() {
        try (InputStream in = getClass()
                .getResourceAsStream("/config.properties")) {
            var props = new Properties();
            props.load(in);
            apiKey = props.getProperty("firebase.apiKey");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load API key", e);
        }
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        mapper = new ObjectMapper();
    }

    public static AuthService getInstance() {
        return INSTANCE;
    }

    public String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public void signOut() {
        idToken = null;
        currentUserEmail = null;
    }

    public CompletableFuture<JsonNode> signUp(String email, String password) {
        var url = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key="
                + apiKey;
        var payload = mapper.createObjectNode()
                .put("email", email)
                .put("password", password)
                .put("returnSecureToken", true);
        return postJson(url, payload.toString())
                .thenApply(json -> {
                    // no need to store token here
                    return json;
                });
    }

    public CompletableFuture<JsonNode> signIn(String email, String password) {
        var url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key="
                + apiKey;
        var payload = mapper.createObjectNode()
                .put("email", email)
                .put("password", password)
                .put("returnSecureToken", true);

        return postJson(url, payload.toString())
                .thenApply(json -> {
                    idToken = json.get("idToken").asText();
                    currentUserEmail = json.get("email").asText();
                    return json;
                });
    }

    public CompletableFuture<Void> resetPassword(String email) {
        var url = "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key="
                + apiKey;
        var payload = mapper.createObjectNode()
                .put("requestType", "PASSWORD_RESET")
                .put("email", email);

        return postJson(url, payload.toString())
                .thenApply(json -> null);
    }

    private CompletableFuture<JsonNode> postJson(String url, String body) {
        var req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    try {
                        var json = mapper.readTree(resp.body());
                        if (resp.statusCode() != 200) {
                            var err = json.path("error")
                                    .path("message")
                                    .asText("Unknown error");
                            throw new RuntimeException(err);
                        }
                        return json;
                    } catch (Exception e) {
                        throw new RuntimeException("Invalid response", e);
                    }
                });
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        // Return the current user's Firebase ID token (refresh if needed)
        return this.idToken; // or however you store it after login
    }
}