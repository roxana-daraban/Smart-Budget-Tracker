package com.budget.backend.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface ExchangeRateClient {
    Optional<BigDecimal> getExchangeRate(String fromCurrency, String toCurrency, LocalDate date);

}
