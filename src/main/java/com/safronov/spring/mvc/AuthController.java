package com.safronov.spring.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import io.jsonwebtoken.Claims;

/**
 * Контроллер для JWT аутентификации
 * Предоставляет эндпоинты для входа и получения токенов
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Эндпоинт для аутентификации и получения JWT токена
     * POST /auth/login
     * Body: {"username": "jwt", "password": "jwt", "salt": "optional_salt"}
     * Header: X-Request-Salt: optional_salt (альтернативный способ передачи соли)
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, 
                                           @RequestHeader(value = "X-Request-Salt", required = false) String headerSalt) {
        try {
            // Аутентифицируем пользователя
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), 
                    loginRequest.getPassword()
                )
            );

            // Получаем UserDetails из аутентификации
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // Определяем соль: сначала из тела запроса, затем из заголовка
            String salt = loginRequest.getSalt();
            if (salt == null || salt.isEmpty()) {
                salt = headerSalt;
            }
            
            // Генерируем JWT токен с солью (если предоставлена)
            String jwt = jwtUtils.generateToken(userDetails, salt);

            // Возвращаем токен в ответе
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("username", userDetails.getUsername());
            response.put("roles", userDetails.getAuthorities());
            response.put("message", "Успешная аутентификация");
            
            // Добавляем информацию о соли
            String extractedSalt = jwtUtils.extractSaltFromToken(jwt);
            response.put("salt_in_token", extractedSalt);
            response.put("salt_auto_generated", salt == null || salt.isEmpty());
            
            if (salt != null && !salt.isEmpty()) {
                response.put("custom_salt_used", salt);
                response.put("salt_source", loginRequest.getSalt() != null ? "body" : "header");
            }

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Неверные учетные данные");
            errorResponse.put("message", "Проверьте логин и пароль");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Эндпоинт для проверки валидности токена
     * GET /auth/validate
     * Header: Authorization: Bearer <token>
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Отсутствует Bearer токен");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        String token = authHeader.substring(7);
        
        if (jwtUtils.isValidJwtToken(token)) {
            String username = jwtUtils.extractUsername(token);
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("username", username);
            response.put("message", "Токен валиден");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Невалидный токен");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Эндпоинт для очистки авторизационных токенов
     * POST /auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        // Если есть токен в заголовке, проверяем его и логируем
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtils.isValidJwtToken(token)) {
                String username = jwtUtils.extractUsername(token);
                response.put("cleared_token_for_user", username);
                System.out.println("Очищен JWT токен для пользователя: " + username);
            }
        }
        
        response.put("message", "Авторизационные токены очищены");
        response.put("status", "success");
        response.put("timestamp", System.currentTimeMillis());
        
        System.out.println("Запрос на очистку токенов обработан");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Эндпоинт для очистки Basic Authentication
     * POST /auth/logout-basic
     */
    @PostMapping("/logout-basic")
    public ResponseEntity<?> logoutBasic(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            response.put("cleared_basic_auth", true);
            System.out.println("Очищена Basic Authentication");
        }
        
        response.put("message", "Basic Authentication очищена");
        response.put("status", "success");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Эндпоинт для проверки Basic Authentication
     * GET /auth/check-basic
     */
    @GetMapping("/check-basic")
    public ResponseEntity<?> checkBasicAuth(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            // Декодируем Basic Auth
            String credentials = authHeader.substring(6);
            String decodedCredentials = new String(java.util.Base64.getDecoder().decode(credentials));
            String[] parts = decodedCredentials.split(":");
            
            if (parts.length == 2 && "basic".equals(parts[0]) && "basic".equals(parts[1])) {
                response.put("authenticated", true);
                response.put("username", "basic");
                response.put("role", "BASIC_USER");
                response.put("message", "Basic Authentication успешна");
                return ResponseEntity.ok(response);
            }
        }
        
        response.put("authenticated", false);
        response.put("message", "Basic Authentication не найдена");
        return ResponseEntity.status(401).body(response);
    }

    /**
     * Эндпоинт для проверки JWT Authentication
     * GET /auth/check-jwt
     */
    @GetMapping("/check-jwt")
    public ResponseEntity<?> checkJwtAuth(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            if (jwtUtils.isValidJwtToken(token)) {
                String username = jwtUtils.extractUsername(token);
                response.put("authenticated", true);
                response.put("username", username);
                response.put("role", "JWT_USER");
                response.put("message", "JWT Authentication успешна");
                return ResponseEntity.ok(response);
            }
        }
        
        response.put("authenticated", false);
        response.put("message", "JWT Authentication не найдена");
        return ResponseEntity.status(401).body(response);
    }

    /**
     * Эндпоинт для получения информации о токене
     * GET /auth/token-info
     */
    @GetMapping("/token-info")
    public ResponseEntity<?> getTokenInfo(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            if (jwtUtils.isValidJwtToken(token)) {
                String username = jwtUtils.extractUsername(token);
                String salt = jwtUtils.extractSaltFromToken(token);
                
                // Получаем информацию о токене
                response.put("token_type", "JWT Bearer Token with Salt");
                response.put("username", username);
                response.put("role", "JWT_USER");
                response.put("token_preview", token.length() > 20 ? token.substring(0, 20) + "..." : token);
                response.put("token_length", token.length());
                response.put("is_valid", true);
                response.put("message", "Токен валиден и активен");
                
                // Добавляем информацию о соли
                response.put("salt_in_token", salt);
                response.put("salt_length", salt != null ? salt.length() : 0);
                response.put("has_salt", salt != null && !salt.isEmpty());
                
                // Добавляем время создания токена
                try {
                    Date issuedAt = jwtUtils.extractExpiration(token);
                    response.put("created_at", issuedAt.toString());
                    response.put("token_id", jwtUtils.extractTokenId(token));
                } catch (Exception e) {
                    response.put("created_at", "Не удалось извлечь время создания");
                    response.put("token_id", "Недоступно");
                }
                
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Невалидный токен");
                response.put("is_valid", false);
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            response.put("error", "Токен не найден в заголовке Authorization");
            response.put("is_valid", false);
            return ResponseEntity.status(401).body(response);
        }
    }

    /**
     * Эндпоинт для получения информации о Basic Authentication
     * GET /auth/basic-info
     */
    @GetMapping("/basic-info")
    public ResponseEntity<?> getBasicAuthInfo(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            try {
                // Декодируем Basic Auth
                String credentials = authHeader.substring(6);
                String decodedCredentials = new String(java.util.Base64.getDecoder().decode(credentials));
                String[] parts = decodedCredentials.split(":");
                
                if (parts.length == 2 && "basic".equals(parts[0]) && "basic".equals(parts[1])) {
                    response.put("auth_type", "HTTP Basic Authentication");
                    response.put("username", "basic");
                    response.put("role", "BASIC_USER");
                    response.put("credentials_preview", "basic:***");
                    response.put("is_valid", true);
                    response.put("message", "Basic Authentication валидна и активна");
                    response.put("method", "Стандартная браузерная форма входа");
                    
                    return ResponseEntity.ok(response);
                } else {
                    response.put("error", "Неверные учетные данные");
                    response.put("is_valid", false);
                    return ResponseEntity.badRequest().body(response);
                }
            } catch (Exception e) {
                response.put("error", "Ошибка декодирования Basic Auth");
                response.put("is_valid", false);
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            response.put("error", "Basic Authentication не найдена в заголовке Authorization");
            response.put("is_valid", false);
            return ResponseEntity.status(401).body(response);
        }
    }

    /**
     * Эндпоинт для проверки Bearer токена из заголовка
     * GET /auth/check-bearer-header
     */
    @GetMapping("/check-bearer-header")
    public ResponseEntity<?> checkBearerFromHeader(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            if (jwtUtils.isValidJwtToken(token)) {
                String username = jwtUtils.extractUsername(token);
                String salt = jwtUtils.extractSaltFromToken(token);
                
                response.put("authenticated", true);
                response.put("username", username);
                response.put("role", "JWT_USER");
                response.put("token_type", "Bearer Token with Salt");
                response.put("token_preview", token.length() > 20 ? token.substring(0, 20) + "..." : token);
                response.put("message", "Bearer токен валиден и активен");
                response.put("salt_in_token", salt);
                response.put("has_salt", salt != null && !salt.isEmpty());
                
                System.out.println("Bearer токен проверен для пользователя: " + username);
                
                return ResponseEntity.ok(response);
            } else {
                response.put("authenticated", false);
                response.put("error", "Невалидный Bearer токен");
                return ResponseEntity.status(401).body(response);
            }
        } else {
            response.put("authenticated", false);
            response.put("error", "Bearer токен не найден в заголовке Authorization");
            return ResponseEntity.status(401).body(response);
        }
    }

    /**
     * Класс для запроса аутентификации
     */
    public static class LoginRequest {
        private String username;
        private String password;
        private String salt; // Добавляем соль

        // Геттеры и сеттеры
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getSalt() {
            return salt;
        }

        public void setSalt(String salt) {
            this.salt = salt;
        }
    }
} 