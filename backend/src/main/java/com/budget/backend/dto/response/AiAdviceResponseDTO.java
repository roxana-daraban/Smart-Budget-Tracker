package com.budget.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiAdviceResponseDTO {
    /** Text generat de Gemini (sfaturi + concluzie) */
    private String adviceText;
}
