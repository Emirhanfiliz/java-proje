package com.sporttracker.shared.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secretKey = "default-sport-tracker-secret-key-change-in-production-256bit";
    private long   accessTokenExpirationMs  = 900_000L;
    private long   refreshTokenExpirationMs = 604_800_000L;

    public String getSecretKey()                   { return secretKey; }
    public void   setSecretKey(String secretKey)   { this.secretKey = secretKey; }

    public long getAccessTokenExpirationMs()                        { return accessTokenExpirationMs; }
    public void setAccessTokenExpirationMs(long accessTokenExpirationMs) { this.accessTokenExpirationMs = accessTokenExpirationMs; }

    public long getRefreshTokenExpirationMs()                         { return refreshTokenExpirationMs; }
    public void setRefreshTokenExpirationMs(long refreshTokenExpirationMs) { this.refreshTokenExpirationMs = refreshTokenExpirationMs; }
}
