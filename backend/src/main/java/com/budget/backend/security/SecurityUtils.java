package com.budget.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Extrage utilizatorul curent din contextul Spring Security (setat de JwtAuthenticationFilter).
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthenticatedUser user) {
            return user.getId();
        }
        return null;
    }
}
