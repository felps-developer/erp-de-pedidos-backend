package com.golden.erp.infrastructure.client.exchange;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateResponse {

    private Boolean success;
    private Map<String, BigDecimal> rates;
}
