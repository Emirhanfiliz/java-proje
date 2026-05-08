package com.sporttracker.desktop.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiClient {

    private static final String     BASE_URL = "http://localhost:8080";
    private static final HttpClient HTTP     = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final Gson GSON = new GsonBuilder().create();

    public static <T> ApiResult<T> post(String path, Object body, Class<T> dataType) {
        return execute(buildPost(path, body, null), dataType);
    }

    public static <T> ApiResult<T> postWithAuth(String path, Object body, String token, Class<T> dataType) {
        return execute(buildPost(path, body, token), dataType);
    }

    public static <T> ApiResult<T> get(String path, String token, Type dataType) {
        return execute(buildGet(path, token), dataType);
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

    private static <T> ApiResult<T> execute(HttpRequest request, Type dataType) {
        try {
            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject json    = JsonParser.parseString(response.body()).getAsJsonObject();
            boolean    success = json.get("success").getAsBoolean();
            String     message = json.has("message") ? json.get("message").getAsString() : "";
            T          data    = null;
            if (json.has("data") && !json.get("data").isJsonNull()) {
                data = GSON.fromJson(json.get("data"), dataType);
            }
            return new ApiResult<>(success, message, data, response.statusCode());
        } catch (Exception e) {
            return new ApiResult<>(false, "Sunucuya bağlanılamadı: " + e.getMessage(), null, 0);
        }
    }
}
