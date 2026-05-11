package com.sporttracker.mobile.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

public class MobileApiService {

    private static final String     BASE_URL = "http://localhost:8084";
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
                .timeout(Duration.ofSeconds(15))
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(body)));
        if (token != null) b.header("Authorization", "Bearer " + token);
        return b.build();
    }

    private static HttpRequest buildGet(String path, String token) {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .timeout(Duration.ofSeconds(15))
                .GET();
        if (token != null) b.header("Authorization", "Bearer " + token);
        return b.build();
    }

    private static <T> T execute(HttpRequest request, Type type) throws Exception {
        try {
            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

            boolean success = false;
            if (json.has("success") && !json.get("success").isJsonNull()) {
                success = json.get("success").getAsBoolean();
            } else if (response.statusCode() >= 200 && response.statusCode() < 300) {
                success = true;
            }

            if (!success) {
                String errorMsg = "Bilinmeyen hata";
                if (json.has("message") && !json.get("message").isJsonNull()) {
                    errorMsg = json.get("message").getAsString();
                } else if (json.has("error") && !json.get("error").isJsonNull()) {
                    errorMsg = json.get("error").getAsString();
                }
                throw new RuntimeException(errorMsg);
            }
            
            return json.has("data") && !json.get("data").isJsonNull()
                    ? GSON.fromJson(json.get("data"), type)
                    : null;
        } catch (ConnectException | HttpTimeoutException e) {
            throw new RuntimeException("Sunucuya ulaşılamadı. İnternetinizi veya servis durumunu kontrol edin.");
        } catch (com.google.gson.JsonSyntaxException e) {
            throw new RuntimeException("Sunucudan geçersiz bir yanıt alındı.");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("İstek işlenemedi. Lütfen tekrar deneyin.");
        }
    }

    public static String toUserMessage(Throwable ex, String fallbackMessage) {
        String message = ex != null && ex.getMessage() != null ? ex.getMessage().trim() : "";
        if (message.isEmpty()) {
            return fallbackMessage;
        }
        return message;
    }
}
