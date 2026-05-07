package com.sporttracker.shared.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secretKey = "default-sport-tracker-secret-key-change-in-production-256bit";
    private long accessTokenExpirationMs = 900_000L;
    private long refreshTokenExpirationMs = 604_800_000L;
}
