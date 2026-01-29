package com.budget.backend.client;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Component
public interface ExchangeRateClient {
    Optional<BigDecimal> getExchangeRate(String fromCurrency, String toCurrency, LocalDate date);

}
