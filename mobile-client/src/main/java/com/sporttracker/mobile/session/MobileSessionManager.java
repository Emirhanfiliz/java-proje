package com.sporttracker.mobile.session;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Base64;
import java.util.Map;

public class MobileSessionManager {

    private static volatile MobileSessionManager instance;

    private String token;
    private String username;
    private String email;
    private String userId;
    
    private Map<String, Object> selectedWorkout;

    private MobileSessionManager() {}

    public static MobileSessionManager getInstance() {
        if (instance == null) {
            synchronized (MobileSessionManager.class) {
                if (instance == null) instance = new MobileSessionManager();
            }
        }
        return instance;
    }

    public void login(String token, String username, String email) {
        this.token = token;
        this.email = email;
        this.userId = extractUserIdFromToken(token);

        String usernameFromToken = extractUsernameFromToken(token);
        if (usernameFromToken != null && !usernameFromToken.isBlank()) {
            this.username = usernameFromToken;
        } else if (username != null && !username.isBlank()) {
            this.username = username;
        } else {
            this.username = email;
        }
    }

    public void logout() {
        token = null; username = null; email = null; userId = null; selectedWorkout = null;
    }

    public boolean isLoggedIn()   { return token != null && !token.isEmpty(); }
    public String  getToken()     { return token; }
    public String  getUsername()  { return username; }
    public String  getEmail()     { return email; }
    public String  getUserId()    { return userId; }

    public Map<String, Object> getSelectedWorkout() { return selectedWorkout; }
    public void setSelectedWorkout(Map<String, Object> selectedWorkout) { this.selectedWorkout = selectedWorkout; }

    private String extractUserIdFromToken(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            byte[] payload = Base64.getUrlDecoder().decode(parts[1]);
            JsonObject claims = JsonParser.parseString(new String(payload)).getAsJsonObject();
            return claims.has("userId") ? claims.get("userId").getAsString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String extractUsernameFromToken(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            byte[] payload = Base64.getUrlDecoder().decode(parts[1]);
            JsonObject claims = JsonParser.parseString(new String(payload)).getAsJsonObject();
            String[] keys = {"username", "preferred_username", "name", "sub"};
            for (String key : keys) {
                if (claims.has(key) && !claims.get(key).isJsonNull()) {
                    String value = claims.get(key).getAsString();
                    if (value != null && !value.isBlank() && !value.contains("@")) {
                        return value;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
