package com.budget.backend.dto.response;

import com.budget.backend.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String username;
    private String email;
    private Role role;
    private Long userId;
    /** Moneda de bază a utilizatorului (ISO 4217) */
    private String baseCurrency;

}
