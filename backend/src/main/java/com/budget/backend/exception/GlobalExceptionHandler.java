package com.budget.backend.exception;

import com.budget.backend.dto.response.ErrorResponseDTO;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler - Gestionare centralizată a erorilor
 *
 * @ControllerAdvice - Marchează clasa ca handler global pentru toate excepțiile
 *
 * Ce face?
 * - Prinde toate excepțiile din aplicație
 * - Transformă excepțiile în răspunsuri HTTP structurate
 * - Returnează status codes corecte și mesaje clare
 *
 * De ce @ControllerAdvice?
 * - Permite gestionarea erorilor pentru toate controller-ele
 * - Nu trebuie să gestionezi erorile în fiecare controller separat
 * - Respectă principiul DRY (Don't Repeat Yourself)
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gestionează excepțiile de validare (@Valid din DTO-uri)
     *
     * @param ex - Excepția de validare
     * @param request - Request-ul HTTP
     * @return ResponseEntity cu eroarea de validare
     *
     * Când se declanșează?
     * - Când un DTO nu trece validarea (@NotBlank, @Email, etc.)
     *
     * Status code: 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponseDTO errorResponse = new ErrorResponseDTO();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setError("Validation Failed");
        errorResponse.setMessage(errors.toString()); // Ex: "{email=Email must be valid, password=Password is required}"
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Gestionează RuntimeException-urile (ex: "Email already exists")
     *
     * @param ex - RuntimeException
     * @param request - Request-ul HTTP
     * @return ResponseEntity cu eroarea
     *
     * Când se declanșează?
     * - Când UserService aruncă RuntimeException (ex: "Email already exists")
     *
     * Status code: 409 Conflict pentru duplicate, 401 Unauthorized pentru credențiale greșite
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleRuntimeException(
            RuntimeException ex, WebRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String error = "Internal Server Error";

        // Determină status code-ul bazat pe mesajul erorii
        String message = ex.getMessage();
        if (message != null) {
            if (message.contains("already exists") || message.contains("already exist")) {
                status = HttpStatus.CONFLICT; // 409
                error = "Conflict";
            } else if (message.contains("Invalid") || message.contains("invalid")) {
                status = HttpStatus.UNAUTHORIZED; // 401
                error = "Unauthorized";
            } else if (message.contains("not found") || message.contains("Not Found")) {
                status = HttpStatus.NOT_FOUND; // 404
                error = "Not Found";
            }
        }

        ErrorResponseDTO errorResponse = new ErrorResponseDTO();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(status.value());
        errorResponse.setError(error);
        errorResponse.setMessage(message != null ? message : "An error occurred");
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Gestionează toate celelalte excepții (catch-all)
     *
     * @param ex - Orice altă excepție
     * @param request - Request-ul HTTP
     * @return ResponseEntity cu eroarea generică
     *
     * Status code: 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception ex, WebRequest request) {

        ErrorResponseDTO errorResponse = new ErrorResponseDTO();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setError("Internal Server Error");
        errorResponse.setMessage("An unexpected error occurred");
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}