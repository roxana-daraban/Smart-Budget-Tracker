package com.budget.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import java.time.Duration;

/**
 * SecurityConfig - Configurare Spring Security
 *
 * @Configuration - Marchează clasa ca configurație Spring
 * @EnableWebSecurity - Activează Spring Security pentru aplicație
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Bean pentru PasswordEncoder - folosit pentru hash-uirea parolelor
     *
     * @Bean - Spring creează o instanță a acestui bean și o gestionează
     *
     * BCryptPasswordEncoder - Implementare BCrypt pentru hash-uirea parolelor
     * - Salt automat (fiecare hash este unic)
     * - Cost factor 10 (2^10 iterații)
     *
     * De ce @Bean?
     * - Permite Spring să injecteze automat PasswordEncoder în UserService
     * - O singură instanță (Singleton) pentru întreaga aplicație
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * SecurityFilterChain - Configurare pentru securitatea aplicației
     *
     * @param http - HttpSecurity pentru configurare
     * @return SecurityFilterChain - Lanțul de filtre de securitate
     * @throws Exception - Dacă configurarea eșuează
     *
     * Configurare:
     * - Disable CSRF (pentru REST API, nu avem nevoie)
     * - Session stateless (folosim JWT, nu session-uri)
     * - Permit toate request-urile (temporar, până implementăm JWT filter)
     */

    /**
     * CORS - Permite request-uri de la frontend (localhost:8081).
     * Fără asta, browser-ul blochează request-urile cross-origin.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:8081", "http://localhost:5173"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-User-Id"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().permitAll());

        return http.build();
    }

    /**
     * RestTemplate pentru apeluri HTTP externe (ex. API curs valutar).
     * În Spring Boot 4, RestTemplateBuilder nu mai expune setConnectTimeout/setReadTimeout,
     * deci folosim SimpleClientHttpRequestFactory și setăm timeout-urile direct pe factory.
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(5));
        return new RestTemplate(factory);
    }
}