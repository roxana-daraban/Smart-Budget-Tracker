package com.budget.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ErrorResponseDTO - DTO pentru răspunsuri de eroare
 *
 * Folosit pentru a returna erori într-un format consistent
 *
 * Format standard:
 * {
 *   "timestamp": "2024-01-27T19:30:00",
 *   "status": 409,
 *   "error": "Conflict",
 *   "message": "Email already exists",
 *   "path": "/api/auth/register"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {

    /**
     * Timestamp-ul când a apărut eroarea
     */
    private LocalDateTime timestamp;

    /**
     * Status code HTTP (ex: 400, 401, 409, 500)
     */
    private int status;

    /**
     * Tipul erorii (ex: "Bad Request", "Conflict", "Unauthorized")
     */
    private String error;

    /**
     * Mesajul de eroare (explicativ pentru frontend)
     */
    private String message;

    /**
     * Path-ul unde a apărut eroarea (ex: "/api/auth/register")
     */
    private String path;
}