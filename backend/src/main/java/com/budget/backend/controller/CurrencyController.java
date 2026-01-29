package com.budget.backend.controller;

import com.budget.backend.dto.request.ConvertRequestDTO;
import com.budget.backend.dto.response.ConversionResponseDTO;
import com.budget.backend.service.CurrencyConversionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/currency")
public class CurrencyController {

    private final CurrencyConversionService currencyConversionService;

    public CurrencyController(CurrencyConversionService currencyConversionService) {
        this.currencyConversionService = currencyConversionService;
    }

    @PostMapping("/convert")
    public ResponseEntity<ConversionResponseDTO> convert(@Valid @RequestBody ConvertRequestDTO request) {
        return ResponseEntity.ok(currencyConversionService.convert(request));
    }
}
