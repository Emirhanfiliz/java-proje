package com.sporttracker.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    public static final String CLAIM_USER_ID    = "userId";
    public static final String CLAIM_EMAIL      = "email";
    public static final String CLAIM_ROLE       = "role";
    public static final String CLAIM_TOKEN_TYPE = "tokenType";

    public static final String TOKEN_TYPE_ACCESS  = "ACCESS";
    public static final String TOKEN_TYPE_REFRESH = "REFRESH";

    private final JwtProperties jwtProperties;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateAccessToken(String userId, String email, String role) {
        Map<String, Object> claims = buildClaims(userId, email, role, TOKEN_TYPE_ACCESS);
        return buildToken(email, claims, jwtProperties.getAccessTokenExpirationMs());
    }

    public String generateRefreshToken(String userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_USER_ID,    userId);
        claims.put(CLAIM_EMAIL,      email);
        claims.put(CLAIM_TOKEN_TYPE, TOKEN_TYPE_REFRESH);
        return buildToken(email, claims, jwtProperties.getRefreshTokenExpirationMs());
    }

    public TokenValidationResult validateToken(String token) {
        try {
            parseAllClaims(token);
            return TokenValidationResult.valid();
        } catch (ExpiredJwtException ex) {
            log.warn("[JWT] Token süresi dolmuş: {}", ex.getMessage());
            return TokenValidationResult.invalid("TOKEN_EXPIRED", "JWT token süresi dolmuş");
        } catch (MalformedJwtException ex) {
            log.warn("[JWT] Hatalı token formatı: {}", ex.getMessage());
            return TokenValidationResult.invalid("MALFORMED_TOKEN", "JWT token formatı geçersiz");
        } catch (SignatureException ex) {
            log.warn("[JWT] Geçersiz imza: {}", ex.getMessage());
            return TokenValidationResult.invalid("INVALID_SIGNATURE", "JWT imzası doğrulanamadı");
        } catch (UnsupportedJwtException ex) {
            log.warn("[JWT] Desteklenmeyen token tipi: {}", ex.getMessage());
            return TokenValidationResult.invalid("UNSUPPORTED_TOKEN", "Desteklenmeyen JWT formatı");
        } catch (IllegalArgumentException ex) {
            log.warn("[JWT] Token boş veya null: {}", ex.getMessage());
            return TokenValidationResult.invalid("EMPTY_TOKEN", "JWT token boş veya null");
        }
    }

    public boolean isAccessToken(String token) {
        try {
            String tokenType = extractClaim(token, claims -> claims.get(CLAIM_TOKEN_TYPE, String.class));
            return TOKEN_TYPE_ACCESS.equals(tokenType);
        } catch (Exception ex) {
            log.debug("[JWT] Token tipi okunamadı: {}", ex.getMessage());
            return false;
        }
    }

    public String extractEmail(String token)  { return extractClaim(token, Claims::getSubject); }
    public String extractUserId(String token) { return extractClaim(token, c -> c.get(CLAIM_USER_ID, String.class)); }
    public String extractRole(String token)   { return extractClaim(token, c -> c.get(CLAIM_ROLE, String.class)); }
    public Date   extractExpiration(String token) { return extractClaim(token, Claims::getExpiration); }

    public boolean isTokenExpired(String token) {
        try { return extractExpiration(token).before(new Date()); }
        catch (ExpiredJwtException ex) { return true; }
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(parseAllClaims(token));
    }

    private Map<String, Object> buildClaims(String userId, String email, String role, String tokenType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_USER_ID,    userId);
        claims.put(CLAIM_EMAIL,      email);
        claims.put(CLAIM_ROLE,       role);
        claims.put(CLAIM_TOKEN_TYPE, tokenType);
        return claims;
    }

    private String buildToken(String subject, Map<String, Object> extra, long expirationMs) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(extra)
                .subject(subject)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    private Claims parseAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] key = jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(key);
    }
}
