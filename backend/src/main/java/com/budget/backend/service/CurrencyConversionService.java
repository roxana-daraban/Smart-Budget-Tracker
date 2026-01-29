package com.budget.backend.service;

import com.budget.backend.client.ExchangeRateClient;
import com.budget.backend.dto.request.ConvertRequestDTO;
import com.budget.backend.dto.response.ConversionResponseDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class CurrencyConversionService {
    private static final int SCALE = 2;

    private final ExchangeRateClient exchangeRateClient;

    public CurrencyConversionService(ExchangeRateClient exchangeRateClient) {
        this.exchangeRateClient = exchangeRateClient;
    }

    public ConversionResponseDTO convert(ConvertRequestDTO request) {
        String from = request.getFromCurrency().toUpperCase();
        String to = request.getToCurrency().toUpperCase();
        BigDecimal amount = request.getAmount();

        Optional<BigDecimal> rateOpt = exchangeRateClient.getExchangeRate(from, to, null);

        if (rateOpt.isEmpty()) {
            throw new RuntimeException("Exchange rate not available for " + from + " -> " + to);
        }

        BigDecimal rate = rateOpt.get();
        BigDecimal converted = amount.multiply(rate).setScale(SCALE, RoundingMode.HALF_UP);

        return ConversionResponseDTO.builder()
                .originalAmount(amount)
                .fromCurrency(from)
                .convertedAmount(converted)
                .toCurrency(to)
                .rate(rate)
                .rateDate(LocalDate.now())
                .build();
    }
}
