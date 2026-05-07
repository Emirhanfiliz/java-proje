package com.sporttracker.shared.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtil {

    public static final String BEARER_PREFIX       = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static String extractTokenFromHeader(String authorizationHeader) {
        if (StringUtils.hasText(authorizationHeader)
                && authorizationHeader.startsWith(BEARER_PREFIX)) {
            return authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        }
        return null;
    }

    public static String addBearerPrefix(String token) {
        if (!StringUtils.hasText(token)) {
            return token;
        }
        if (token.startsWith(BEARER_PREFIX)) {
            return token;
        }
        return BEARER_PREFIX + token;
    }

    public static boolean isBearerToken(String authorizationHeader) {
        return StringUtils.hasText(authorizationHeader)
                && authorizationHeader.startsWith(BEARER_PREFIX);
    }

    public static boolean hasJwtStructure(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        String[] parts = token.split("\\.");
        return parts.length == 3
                && StringUtils.hasText(parts[0])
                && StringUtils.hasText(parts[1])
                && StringUtils.hasText(parts[2]);
    }
}
