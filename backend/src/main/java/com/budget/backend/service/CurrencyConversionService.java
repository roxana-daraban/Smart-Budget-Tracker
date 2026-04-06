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
        LocalDate rateDate = LocalDate.now();
        if (from.equals(to)) {
            return ConversionResponseDTO.builder()
                    .originalAmount(amount)
                    .fromCurrency(from)
                    .convertedAmount(amount.setScale(SCALE, RoundingMode.HALF_UP))
                    .toCurrency(to)
                    .rate(BigDecimal.ONE)
                    .rateDate(rateDate)
                    .build();
        }
        Optional<BigDecimal> rateOpt = exchangeRateClient.getExchangeRate(from, to, rateDate);
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
                .rateDate(rateDate)
                .build();
    }

    /**
     * Convertește o sumă între două monede, folosind cursul pentru data dată (sau cel mai recent disponibil).
     */
    public BigDecimal convertAmount(BigDecimal amount, String fromCurrency, String toCurrency, LocalDate rateDate) {
        String from = fromCurrency.toUpperCase();
        String to = toCurrency.toUpperCase();
        if (from.equals(to)) {
            return amount.setScale(SCALE, RoundingMode.HALF_UP);
        }
        Optional<BigDecimal> rateOpt = exchangeRateClient.getExchangeRate(from, to, rateDate != null ? rateDate : LocalDate.now());
        if (rateOpt.isEmpty()) {
            throw new RuntimeException("Exchange rate not available for " + from + " -> " + to);
        }
        return amount.multiply(rateOpt.get()).setScale(SCALE, RoundingMode.HALF_UP);
    }
}
