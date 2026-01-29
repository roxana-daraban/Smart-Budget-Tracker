package com.budget.backend.client.impl;

import com.budget.backend.client.ExchangeRateClient;
import com.budget.backend.dto.response.FrankfurterResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/** Spring creează un bean pentru ExchangeRateClient; fără @Component nu există bean și CurrencyConversionService eșuează. */
@Component
public class FrankfurterExchangeRateClient implements ExchangeRateClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public FrankfurterExchangeRateClient(
            RestTemplate restTemplate,
            @Value("${currency.api.base-url:https://api.frankfurter.app}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public Optional<BigDecimal> getExchangeRate(String fromCurrency, String toCurrency, LocalDate date) {
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return Optional.of(BigDecimal.ONE);
        }
        String url = baseUrl + "/latest?from=" + fromCurrency + "&to=" + toCurrency;
        try {
            FrankfurterResponseDTO response = restTemplate.getForObject(url, FrankfurterResponseDTO.class);
            if (response != null && response.getRates() != null && response.getRates().containsKey(toCurrency)) {
                return Optional.of(response.getRates().get(toCurrency));
            }
        } catch (Exception e) {
            // Log and return empty; service layer will handle
        }
        return Optional.empty();
    }
}
