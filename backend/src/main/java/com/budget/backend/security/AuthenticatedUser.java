package com.budget.backend.security;

import java.io.Serializable;

/**
 * Principal plasat în SecurityContext după validarea JWT.
 */
public class AuthenticatedUser implements Serializable {

    private final Long id;
    private final String username;

    public AuthenticatedUser(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
