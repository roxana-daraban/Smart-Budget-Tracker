package com.budget.backend.controller;

import com.budget.backend.client.GeminiClient;
import com.budget.backend.dto.request.AiAdviceRequestDTO;
import com.budget.backend.dto.response.AiAdviceResponseDTO;
import com.budget.backend.security.SecurityUtils;
import com.budget.backend.service.FinancialReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final FinancialReportService financialReportService;
    private final GeminiClient geminiClient;

    public ReportController(FinancialReportService financialReportService, GeminiClient geminiClient) {
        this.financialReportService = financialReportService;
        this.geminiClient = geminiClient;
    }

    @PostMapping("/ai-advice")
    public ResponseEntity<AiAdviceResponseDTO> getAiAdvice(@Valid @RequestBody AiAdviceRequestDTO request) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new AccessDeniedException("Not authenticated");
        }

        if (request.getTo().isBefore(request.getFrom())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "End date must be on or after start date");
        }

        String prompt = financialReportService.buildAiPrompt(userId, request.getFrom(), request.getTo());
        String advice = geminiClient.generateContent(prompt);
        return ResponseEntity.ok(new AiAdviceResponseDTO(advice));
    }
}
