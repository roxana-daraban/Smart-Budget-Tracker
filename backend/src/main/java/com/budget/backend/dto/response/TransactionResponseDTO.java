package com.budget.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/*
 * Nu expunem User-ul complet, doar ID-ul (pentru securitate)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {

    private Long id;
    private String description;
    private BigDecimal amount;
    private String currency;
    private LocalDate date;
    private Long categoryId;
    private String categoryName;
    private String categoryType; // "INCOME" sau "EXPENSE"
    private Long userId;
}