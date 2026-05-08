package com.sporttracker.mobile.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class MobileApiService {

    private static final String     BASE_URL = "http://localhost:8080";
    private static final HttpClient HTTP     = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final Gson GSON = new Gson();

    public static <T> T post(String path, Object body, Class<T> type) throws Exception {
        return execute(buildPost(path, body, null), type);
    }

    public static <T> T postAuth(String path, Object body, String token, Class<T> type) throws Exception {
        return execute(buildPost(path, body, token), type);
    }

    public static <T> T get(String path, String token, Type type) throws Exception {
        return execute(buildGet(path, token), type);
    }

    private static HttpRequest buildPost(String path, Object body, String token) {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(body)));
        if (token != null) b.header("Authorization", "Bearer " + token);
        return b.build();
    }

    private static HttpRequest buildGet(String path, String token) {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET();
        if (token != null) b.header("Authorization", "Bearer " + token);
        return b.build();
    }

    private static <T> T execute(HttpRequest request, Type type) throws Exception {
        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        if (!json.get("success").getAsBoolean()) {
            throw new RuntimeException(json.has("message") ? json.get("message").getAsString() : "API error");
        }
        return json.has("data") && !json.get("data").isJsonNull()
                ? GSON.fromJson(json.get("data"), type)
                : null;
    }
}
