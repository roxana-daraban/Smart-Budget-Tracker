package com.budget.backend.dto.request;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConvertRequestDTO {
    @NotBlank(message = "Source currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3-letter ISO code")
    private String fromCurrency;

    @NotBlank(message = "Target currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3-letter ISO code")
    private String toCurrency;

    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;
}
