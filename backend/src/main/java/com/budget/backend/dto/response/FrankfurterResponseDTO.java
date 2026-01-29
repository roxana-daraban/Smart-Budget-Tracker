package com.budget.backend.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FrankfurterResponseDTO {

    private String base;
    private String date;

    @JsonProperty("rates")
    private Map<String, BigDecimal> rates;
}
