package com.safronov.spring.mvc;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Утилиты для работы с JWT токенами
 * Используется для Bearer токен авторизации
 * Автоматически добавляет соль в начало каждого токена
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret:mySecretKey}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 часа по умолчанию
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Извлекает имя пользователя из токена
     * Удаляет соль из начала токена перед парсингом
     */
    public String extractUsername(String token) {
        String cleanToken = removeSaltFromToken(token);
        return extractClaim(cleanToken, Claims::getSubject);
    }

    /**
     * Извлекает дату истечения токена
     * Удаляет соль из начала токена перед парсингом
     */
    public Date extractExpiration(String token) {
        String cleanToken = removeSaltFromToken(token);
        return extractClaim(cleanToken, Claims::getExpiration);
    }

    /**
     * Извлекает ID токена
     * Удаляет соль из начала токена перед парсингом
     */
    public String extractTokenId(String token) {
        String cleanToken = removeSaltFromToken(token);
        return extractClaim(cleanToken, Claims::getId);
    }

    /**
     * Извлекает конкретное утверждение из токена
     * Удаляет соль из начала токена перед парсингом
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        String cleanToken = removeSaltFromToken(token);
        final Claims claims = extractAllClaims(cleanToken);
        return claimsResolver.apply(claims);
    }

    /**
     * Извлекает все утверждения из токена
     * Удаляет соль из начала токена перед парсингом
     */
    private Claims extractAllClaims(String token) {
        String cleanToken = removeSaltFromToken(token);
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(cleanToken)
                .getBody();
    }

    /**
     * Проверяет, истек ли токен
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Генерирует токен для пользователя с автоматической солью
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        String jwtToken = createToken(claims, userDetails.getUsername());
        return addSaltToToken(jwtToken);
    }

    /**
     * Генерирует токен для пользователя с указанной солью
     */
    public String generateToken(UserDetails userDetails, String salt) {
        Map<String, Object> claims = new HashMap<>();
        if (salt != null && !salt.isEmpty()) {
            claims.put("request_salt", salt); // Добавляем соль из запроса как claim
        }
        String jwtToken = createToken(claims, userDetails.getUsername());
        return addSaltToToken(jwtToken, salt);
    }

    /**
     * Создает JWT токен
     */
    private String createToken(Map<String, Object> claims, String subject) {
        // Добавляем случайные данные для уникальности токена
        claims.put("jti", java.util.UUID.randomUUID().toString()); // JWT ID
        claims.put("iat", System.currentTimeMillis() / 1000); // Issued At (время создания в секундах)
        claims.put("nbf", System.currentTimeMillis() / 1000); // Not Before (время начала действия в секундах)
        claims.put("random", System.nanoTime()); // Дополнительная случайность
        claims.put("salt", java.util.UUID.randomUUID().toString()); // Соль для уникальности
        claims.put("timestamp", System.currentTimeMillis()); // Точное время в миллисекундах
        
        long currentTime = System.currentTimeMillis();
        
        // Добавляем криптографически стойкую случайность
        SecureRandom secureRandom = new SecureRandom();
        claims.put("nonce", secureRandom.nextLong()); // Криптографически стойкое случайное число
        claims.put("entropy", secureRandom.nextLong()); // Дополнительная энтропия
        
        // Добавляем уникальную комбинацию времени и случайности
        claims.put("unique_id", currentTime + "_" + secureRandom.nextLong());
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(currentTime))
                .setNotBefore(new Date(currentTime)) // Устанавливаем время начала действия
                .setExpiration(new Date(currentTime + jwtExpiration))
                .setId(java.util.UUID.randomUUID().toString()) // Добавляем уникальный ID для каждого токена
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Добавляет автоматическую соль в начало токена
     */
    private String addSaltToToken(String jwtToken) {
        String autoSalt = generateAutoSalt();
        return autoSalt + "." + jwtToken;
    }

    /**
     * Добавляет указанную соль в начало токена
     */
    private String addSaltToToken(String jwtToken, String customSalt) {
        String salt = (customSalt != null && !customSalt.isEmpty()) ? customSalt : generateAutoSalt();
        return salt + "." + jwtToken;
    }

    /**
     * Генерирует автоматическую соль
     */
    private String generateAutoSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] saltBytes = new byte[16];
        secureRandom.nextBytes(saltBytes);
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(saltBytes);
    }

    /**
     * Удаляет соль из начала токена
     */
    private String removeSaltFromToken(String token) {
        if (token == null || token.isEmpty()) {
            return token;
        }
        
        // Ищем точку, которая разделяет соль и JWT токен
        int dotIndex = token.indexOf('.');
        if (dotIndex > 0) {
            // Проверяем, что после точки идет валидный JWT формат (3 части, разделенные точками)
            String afterDot = token.substring(dotIndex + 1);
            String[] parts = afterDot.split("\\.");
            if (parts.length == 3) { // JWT имеет 3 части: header.payload.signature
                return afterDot;
            }
        }
        
        // Если формат не соответствует ожидаемому, возвращаем исходный токен
        return token;
    }

    /**
     * Проверяет валидность токена для пользователя
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Проверяет, является ли строка валидным JWT токеном
     */
    public boolean isValidJwtToken(String token) {
        try {
            String cleanToken = removeSaltFromToken(token);
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(cleanToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Извлекает соль из токена
     */
    public String extractSaltFromToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        
        int dotIndex = token.indexOf('.');
        if (dotIndex > 0) {
            return token.substring(0, dotIndex);
        }
        
        return null;
    }
} 