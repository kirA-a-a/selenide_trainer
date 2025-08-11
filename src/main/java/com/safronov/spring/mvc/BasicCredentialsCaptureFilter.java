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

        try {
            String uri = request.getRequestURI();
            if (uri != null && uri.endsWith("/basic_authentication.html")) {
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith(BASIC_PREFIX)) {
                    String base64Creds = authHeader.substring(BASIC_PREFIX.length());

                    // Сохраняем в cookie, доступную JS (HttpOnly = false)
                    Cookie cookie = new Cookie(COOKIE_NAME, base64Creds);
                    cookie.setPath("/");
                    cookie.setMaxAge(60 * 10); // 10 минут достаточно для тренажёра
                    cookie.setHttpOnly(false);
                    cookie.setSecure(false);
                    response.addCookie(cookie);
                    log.debug("Saved Basic credentials to cookie for basic_authentication.html");
                }
            }
        } catch (Exception e) {
            log.warn("Failed to capture Basic credentials: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}


