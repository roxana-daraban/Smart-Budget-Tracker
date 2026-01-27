package com.budget.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Disable CSRF pentru REST API
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Stateless (folosim JWT)
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().permitAll()  // Temporar: permit toate request-urile
                );

        return http.build();
    }
}