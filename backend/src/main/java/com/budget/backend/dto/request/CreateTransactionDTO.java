package com.budget.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionDTO {
    /**
     * Descrierea tranzacției
     */
    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    /**
     * Suma tranzacției
     *
     * @DecimalMin - Validează că suma este minim 0.01 (nu poți avea tranzacție de 0)
     * @NotNull - Nu poate fi null
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    private BigDecimal amount;

    /**
     * Moneda tranzacției (ex: "RON", "EUR", "USD")
     */
    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters (ISO 4217 code)")
    private String currency;

    /**
     * Data tranzacției
     */
    @NotNull(message = "Date is required")
    private LocalDate date;

    /**
     * ID-ul categoriei
     */
    @NotNull(message = "Category ID is required")
    private Long categoryId;
}
