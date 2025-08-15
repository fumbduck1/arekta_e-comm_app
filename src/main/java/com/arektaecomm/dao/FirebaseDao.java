package com.arektaecomm.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class FirebaseDao<T> {
    private final String databaseUrl;
    private final String entityPath;
    private final Class<T> type;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    public FirebaseDao(String databaseUrl, String entityPath, Class<T> type) {
        this.databaseUrl = databaseUrl.endsWith("/") ? databaseUrl : databaseUrl + "/";
        this.entityPath = entityPath;
        this.type = type;
    }

    private String getAuthParam() {
        String idToken = com.arektaecomm.service.AuthService.getInstance().getIdToken();
        return (idToken != null && !idToken.isEmpty()) ? "?auth=" + idToken : "";
    }

    public void create(String id, T entity) throws Exception {
        String json = mapper.writeValueAsString(entity);
        String url = databaseUrl + entityPath + "/" + id + ".json" + getAuthParam();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    public T fetchById(String id) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(databaseUrl + entityPath + "/" + id + ".json"))
                .GET()
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(resp.body(), type);
    }

    public List<T> fetchAll() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(databaseUrl + entityPath + ".json"))
                .GET()
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        List<T> result = new ArrayList<>();
        if (resp.body() != null && !resp.body().equals("null")) {
            JsonNode node = mapper.readTree(resp.body());
            node.fields().forEachRemaining(entry -> {
                try {
                    result.add(mapper.treeToValue(entry.getValue(), type));
                } catch (Exception ignored) {
                }
            });
        }
        return result;
    }

    public void update(String id, T entity) throws Exception {
        create(id, entity); // Overwrite
    }

    public void delete(String id) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(databaseUrl + entityPath + "/" + id + ".json"))
                .DELETE()
                .build();
        client.send(req, HttpResponse.BodyHandlers.ofString());
    }
}