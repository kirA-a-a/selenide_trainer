package com.safronov.spring.mvc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletResponse;
import com.safronov.spring.mvc.BasicCredentialsCaptureFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Настройка SecurityFilterChain...");
        
        http
            .authorizeHttpRequests(authz -> authz
                // Разрешаем все HTML файлы в корне (самое первое правило!)
                .requestMatchers("/*.html").permitAll()
                
                // Явно разрешаем text_input.html
                .requestMatchers("/text_input.html").permitAll()
                
                // Разрешаем все статические ресурсы в корне
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                
                // Главная страница доступна всем
                .requestMatchers("/", "/basic-auth", "/askDetails", "/showDetails").permitAll()
                
                // Статические ресурсы доступны всем
                .requestMatchers("/index.html", "/jwt_authentication.html", "/askDetails.html", "/showDetails.html", "/localstorage_training.html", "/sessionstorage_training.html").permitAll()
                
                // Разрешаем все запросы к корню
                .requestMatchers("/").permitAll()
                
                // Все auth эндпоинты доступны всем
                .requestMatchers("/auth/**").permitAll()
                
                // Basic Auth страница требует роль BASIC_USER (обрабатывается контроллером)
                .requestMatchers("/basic_authentication.html").hasRole("BASIC_USER")
                
                // API эндпоинты требуют соответствующую роль
                .requestMatchers("/api/basic/**").hasRole("BASIC_USER")
                .requestMatchers("/api/jwt/**").hasRole("JWT_USER")
                
                // Все остальные запросы требуют аутентификации
                .anyRequest().authenticated()
            )
            // Включаем HTTP Basic Authentication
            .httpBasic(basic -> basic
                .realmName("Safronov_ID Basic Auth")
                .authenticationEntryPoint((request, response, authException) -> {
                    log.info("Запрос требует аутентификации: {} {}", request.getMethod(), request.getRequestURI());
                    log.info("User-Agent: {}", request.getHeader("User-Agent"));
                    log.info("Authorization header: {}", request.getHeader("Authorization"));
                    
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setHeader("WWW-Authenticate", "Basic realm=\"Safronov_ID Basic Auth\"");
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Basic Authentication required\"}");
                    
                    log.info("Отправлен ответ 401 с заголовком WWW-Authenticate");
                })
            )
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new BasicCredentialsCaptureFilter(), UsernamePasswordAuthenticationFilter.class);
        
        log.info("SecurityFilterChain настроен");
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        log.info("Создание AuthenticationProvider...");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        log.info("AuthenticationProvider создан");
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        log.info("Создание AuthenticationManager...");
        AuthenticationManager manager = config.getAuthenticationManager();
        log.info("AuthenticationManager создан");
        return manager;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        log.info("Создание JwtAuthenticationFilter...");
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtils, userDetailsService());
        log.info("JwtAuthenticationFilter создан");
        return filter;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        log.info("Создание UserDetailsService...");
        
        UserDetails basicUser = User.builder()
            .username("basic")
            .password(passwordEncoder().encode("basic"))
            .roles("BASIC_USER")
            .build();

        UserDetails jwtUser = User.builder()
            .username("jwt")
            .password(passwordEncoder().encode("jwt"))
            .roles("JWT_USER")
            .build();

        log.info("Создан пользователь basic с ролью BASIC_USER");
        log.info("Создан пользователь jwt с ролью JWT_USER");
        
        return new InMemoryUserDetailsManager(basicUser, jwtUser);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("Создание PasswordEncoder (BCrypt)...");
        return new BCryptPasswordEncoder();
    }
} 