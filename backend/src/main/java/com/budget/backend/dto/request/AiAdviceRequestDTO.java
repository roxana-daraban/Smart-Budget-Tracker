package com.budget.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiAdviceRequestDTO {

    @NotNull(message = "from date is required")
    private LocalDate from;

    @NotNull(message = "to date is required")
    private LocalDate to;
}
