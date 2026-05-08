package com.sporttracker.mobile.session;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Base64;

public class MobileSessionManager {

    private static volatile MobileSessionManager instance;

    private String token;
    private String username;
    private String email;
    private String userId;

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
        this.token    = token;
        this.username = username;
        this.email    = email;
        this.userId   = extractUserIdFromToken(token);
    }

    public void logout() {
        token = null; username = null; email = null; userId = null;
    }

    public boolean isLoggedIn()   { return token != null && !token.isEmpty(); }
    public String  getToken()     { return token; }
    public String  getUsername()  { return username; }
    public String  getEmail()     { return email; }
    public String  getUserId()    { return userId; }

    private String extractUserIdFromToken(String jwt) {
        try {
            String[]   parts   = jwt.split("\\.");
            byte[]     payload = Base64.getUrlDecoder().decode(parts[1]);
            JsonObject claims  = JsonParser.parseString(new String(payload)).getAsJsonObject();
            return claims.has("userId") ? claims.get("userId").getAsString() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
