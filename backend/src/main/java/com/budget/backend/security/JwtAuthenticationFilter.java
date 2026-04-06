package com.budget.backend.security;

import com.budget.backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Validează JWT din Authorization: Bearer &lt;token&gt; și populează SecurityContext.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        if (!jwtService.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        Long userId = jwtService.getUserIdFromToken(token);
        String username = jwtService.getUsernameFromToken(token);
        String role = jwtService.getRoleFromToken(token);

        if (userId == null || username == null) {
            filterChain.doFilter(request, response);
            return;
        }

        AuthenticatedUser principal = new AuthenticatedUser(userId, username);
        var authority = new SimpleGrantedAuthority(
                role != null && !role.isBlank() ? "ROLE_" + role : "ROLE_USER");
        var authToken = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                Collections.singletonList(authority));
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
