package com.aws.dashboard.api.infrastructure.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@Order(1)
public class RateLimitFilter implements Filter {

    // Caché acotada con desalojo LRU y TTL de 10 min para prevenir ataques de agotamiento de memoria (OOM) en 512MB RAM
    private final Cache<String, Bucket> ipBuckets = Caffeine.newBuilder()
            .maximumSize(5_000)
            .expireAfterAccess(Duration.ofMinutes(10))
            .build();

    private Bucket createBucketForIp() {
        // Límite: 120 peticiones por minuto por cliente IP
        Bandwidth limit = Bandwidth.classic(120, Refill.greedy(120, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();

        // Omitir endpoints de monitoreo, salud y documentación OpenAPI/Swagger
        if (uri.startsWith("/actuator") || uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs")) {
            chain.doFilter(request, response);
            return;
        }

        String clientIp = resolveClientIp(req);
        Bucket bucket = ipBuckets.get(clientIp, k -> createBucketForIp());

        if (bucket != null && bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            res.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            res.setContentType("application/problem+json");
            res.getWriter().write("""
                {
                  "type": "https://api.aws-catalog.com/errors/too-many-requests",
                  "title": "Too Many Requests",
                  "status": 429,
                  "detail": "Has excedido el límite de 120 peticiones por minuto. Intenta más tarde."
                }
            """);
        }
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
