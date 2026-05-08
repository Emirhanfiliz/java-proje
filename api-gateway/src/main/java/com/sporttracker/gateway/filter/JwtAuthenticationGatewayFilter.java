package com.sporttracker.gateway.filter;

import com.sporttracker.shared.security.JwtProperties;
import com.sporttracker.shared.security.JwtTokenProvider;
import com.sporttracker.shared.security.JwtUtil;
import com.sporttracker.shared.security.TokenValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@EnableConfigurationProperties(JwtProperties.class)
public class JwtAuthenticationGatewayFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationGatewayFilter.class);

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationGatewayFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    private static final List<String> OPEN_PATHS = List.of(
            "/auth/login",
            "/auth/register"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (isOpenPath(path)) {
            log.debug("[Gateway] Açık rota, JWT kontrolü atlandı: {}", path);
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(JwtUtil.AUTHORIZATION_HEADER);

        if (!JwtUtil.isBearerToken(authHeader)) {
            log.warn("[Gateway] Authorization header eksik veya geçersiz format: {}", path);
            return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Authorization header eksik");
        }

        String token = JwtUtil.extractTokenFromHeader(authHeader);

        TokenValidationResult result = jwtTokenProvider.validateToken(token);

        if (!result.isValid()) {
            log.warn("[Gateway] Token doğrulama başarısız [{}]: {}", result.getErrorCode(), path);
            return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, result.getErrorMessage());
        }

        if (!jwtTokenProvider.isAccessToken(token)) {
            log.warn("[Gateway] Refresh token ile istek yapılamaz: {}", path);
            return writeErrorResponse(exchange, HttpStatus.FORBIDDEN, "Refresh token ile istek yapılamaz");
        }

        String userId = jwtTokenProvider.extractUserId(token);
        String email  = jwtTokenProvider.extractEmail(token);
        String role   = jwtTokenProvider.extractRole(token);

        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Id",    userId)
                .header("X-User-Email", email)
                .header("X-User-Role",  role)
                .build();

        log.debug("[Gateway] Token geçerli, istek yönlendiriliyor. userId={}, path={}", userId, path);
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private boolean isOpenPath(String path) {
        return OPEN_PATHS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\"success\":false,\"message\":\"%s\",\"statusCode\":%d}",
                message, status.value()
        );

        byte[] bytes = body.getBytes();
        org.springframework.core.io.buffer.DataBuffer buffer =
                response.bufferFactory().wrap(bytes);

        return response.writeWith(Mono.just(buffer));
    }
}
