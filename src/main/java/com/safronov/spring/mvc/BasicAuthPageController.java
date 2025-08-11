package com.safronov.spring.mvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Controller
public class BasicAuthPageController {
    
    private static final Logger log = LoggerFactory.getLogger(BasicAuthPageController.class);

    @GetMapping(value = "/basic_authentication.html", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getBasicAuthPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("BasicAuthPageController: Handling request for basic_authentication.html");
        
        // Принудительно проверяем Basic Auth
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            log.warn("No Basic Auth header found, sending 401");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader("WWW-Authenticate", "Basic realm=\"Basic Authentication Required\"");
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("""
                <html>
                <head><title>Access Denied</title></head>
                <body>
                    <h1>Access Denied</h1>
                    <p>This page requires Basic Authentication.</p>
                    <p>Please provide valid credentials (basic:basic) to continue.</p>
                    <p><strong>Note:</strong> Pressing Cancel will NOT grant access.</p>
                </body>
                </html>
                """);
            return null;
        }
        
        // Проверяем креды
        try {
            String base64Credentials = authHeader.substring(6);
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            String[] parts = credentials.split(":");
            
            if (parts.length == 2 && "basic".equals(parts[0]) && "basic".equals(parts[1])) {
                log.info("Valid Basic Auth credentials found, serving page");
                // Читаем и возвращаем HTML страницу
                ClassPathResource resource = new ClassPathResource("static/basic_authentication.html");
                return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            } else {
                log.warn("Invalid Basic Auth credentials: user={}", parts.length > 0 ? parts[0] : "unknown");
            }
        } catch (Exception e) {
            log.error("Error decoding Basic Auth", e);
        }
        
        // Неверные креды
        log.warn("Invalid credentials, sending 401");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("WWW-Authenticate", "Basic realm=\"Basic Authentication Required\"");
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("""
            <html>
            <head><title>Access Denied</title></head>
            <body>
                <h1>Access Denied</h1>
                <p>Invalid credentials provided.</p>
                <p>Please use: username=basic, password=basic</p>
            </body>
            </html>
            """);
        return null;
    }
}
