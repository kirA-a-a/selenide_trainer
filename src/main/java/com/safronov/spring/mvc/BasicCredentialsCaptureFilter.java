package com.safronov.spring.mvc;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Перехватывает запрос на страницу basic_authentication.html.
 * Если присутствует заголовок Authorization с Basic, сохраняет кодированные учетные данные
 * в cookie, чтобы фронтенд мог их использовать для XHR-запросов.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BasicCredentialsCaptureFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(BasicCredentialsCaptureFilter.class);
    private static final String BASIC_PREFIX = "Basic ";
    private static final String COOKIE_NAME = "basic_auth_credentials";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String method = request.getMethod();
        String userAgent = request.getHeader("User-Agent");
        String authHeader = request.getHeader("Authorization");
        
        log.info("=== BasicCredentialsCaptureFilter ===");
        log.info("URI: {}", uri);
        log.info("Method: {}", method);
        log.info("User-Agent: {}", userAgent);
        log.info("Authorization header: {}", authHeader);
        log.info("Request headers:");
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            log.info("  {}: {}", headerName, headerValue);
        }

        try {
            if (uri != null && uri.endsWith("/basic_authentication.html")) {
                log.info("Обрабатывается запрос к basic_authentication.html");
                
                if (authHeader != null && authHeader.startsWith(BASIC_PREFIX)) {
                    String base64Creds = authHeader.substring(BASIC_PREFIX.length());
                    log.info("Найден Basic Auth заголовок, креды: {}", base64Creds);

                    // Сохраняем в cookie, доступную JS (HttpOnly = false)
                    Cookie cookie = new Cookie(COOKIE_NAME, base64Creds);
                    cookie.setPath("/");
                    cookie.setMaxAge(60 * 10); // 10 минут достаточно для тренажёра
                    cookie.setHttpOnly(false);
                    cookie.setSecure(false);
                    response.addCookie(cookie);
                    log.info("Basic credentials сохранены в cookie: {}", COOKIE_NAME);
                } else {
                    log.info("Basic Auth заголовок не найден или неверный формат");
                }
            } else {
                log.info("URI {} не соответствует basic_authentication.html, пропускаем", uri);
            }
        } catch (Exception e) {
            log.error("Ошибка в BasicCredentialsCaptureFilter: {}", e.getMessage(), e);
        }

        log.info("Передаем управление следующему фильтру");
        filterChain.doFilter(request, response);
    }
}


