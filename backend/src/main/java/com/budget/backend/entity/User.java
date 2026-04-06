package com.budget.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER; // Default value: USER

    /** Moneda de bază pentru rapoarte (ISO 4217), implicit RON */
    @Column(name = "base_currency", nullable = false, length = 3)
    private String baseCurrency = "RON";

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (baseCurrency == null || baseCurrency.isBlank()) {
            baseCurrency = "RON";
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        if (baseCurrency == null || baseCurrency.isBlank()) {
            baseCurrency = "RON";
        }
        updatedAt = LocalDateTime.now();
    }
}
