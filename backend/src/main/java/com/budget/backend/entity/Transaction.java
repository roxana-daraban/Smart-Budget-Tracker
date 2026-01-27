package com.budget.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    // precision = 19, scale = 2 înseamnă: maxim 19 cifre totale, 2 după virgulă
    // Exemplu: 9999999999999999.99 (17 cifre înainte + 2 după = 19 total)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency; // ISO 4217 currency codes (RON, EUR, USD, etc.)

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    /*
     * Categoria tranzacției
     * @ManyToOne - Multe tranzacții aparțin unei singure categorii
     * Relationship: Many Transactions -> One Category
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

}
