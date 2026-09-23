package com.aws.dashboard.api.infrastructure.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(2)
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Cabeceras de seguridad OWASP
        res.setHeader("X-Content-Type-Options", "nosniff");
        res.setHeader("X-Frame-Options", "DENY");
        res.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

        // Cabeceras de caché HTTP para el catálogo (solo para peticiones GET en /api/**)
        if ("GET".equalsIgnoreCase(req.getMethod()) && req.getRequestURI().startsWith("/api/")) {
            res.setHeader("Cache-Control", "public, max-age=3600, stale-while-revalidate=86400");
        }

        chain.doFilter(request, response);
    }
}
