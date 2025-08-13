package com.safronov.spring.mvc;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * JWT фильтр для обработки Bearer токенов
 * Извлекает токен из заголовка Authorization и устанавливает аутентификацию
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        
        String uri = request.getRequestURI();
        String method = request.getMethod();
        
        log.info("=== JwtAuthenticationFilter ===");
        log.info("URI: {}", uri);
        log.info("Method: {}", method);
        log.info("Authorization header: {}", authHeader);

        // Проверяем наличие Bearer токена в заголовке
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("Bearer токен не найден, пропускаем запрос");
            filterChain.doFilter(request, response);
            return;
        }

        // Извлекаем токен (убираем "Bearer " префикс)
        jwt = authHeader.substring(7);
        log.info("Найден Bearer токен, длина: {}", jwt.length());
        
        try {
            // Извлекаем имя пользователя из токена
            username = jwtUtils.extractUsername(jwt);
            log.info("Извлечено имя пользователя из токена: {}", username);
            
            // Если имя пользователя найдено и аутентификация еще не установлена
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.info("Загружаем UserDetails для пользователя: {}", username);
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                
                // Проверяем валидность токена
                if (jwtUtils.validateToken(jwt, userDetails)) {
                    log.info("JWT токен валиден для пользователя: {}", username);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Аутентификация установлена для пользователя: {} с ролями: {}", 
                            username, userDetails.getAuthorities());
                } else {
                    log.warn("JWT токен невалиден для пользователя: {}", username);
                }
            } else {
                if (username == null) {
                    log.warn("Не удалось извлечь имя пользователя из токена");
                } else {
                    log.info("Аутентификация уже установлена для пользователя: {}", username);
                }
            }
        } catch (Exception e) {
            // Логируем ошибку, но не прерываем выполнение фильтра
            log.error("Ошибка при обработке JWT токена: {}", e.getMessage(), e);
        }
        
        log.info("Передаем управление следующему фильтру");
        filterChain.doFilter(request, response);
    }
} 