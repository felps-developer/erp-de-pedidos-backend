package com.golden.erp.infrastructure.client.exchange;

import com.golden.erp.application.order.port.ExchangeRatePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeRateAdapter implements ExchangeRatePort {

    private final ExchangeRateClient exchangeRateClient;

    private BigDecimal cachedRate;
    private LocalDateTime cacheExpiry;

    @Override
    public BigDecimal getBrlToUsdRate() {
        if (cachedRate != null && cacheExpiry != null && LocalDateTime.now().isBefore(cacheExpiry)) {
            return cachedRate;
        }

        try {
            ExchangeRateResponse response = exchangeRateClient.getLatestRates("BRL", "USD");
            if (response != null && response.getRates() != null && response.getRates().containsKey("USD")) {
                cachedRate = response.getRates().get("USD").setScale(6, RoundingMode.HALF_UP);
                cacheExpiry = LocalDateTime.now().plusHours(1);
                return cachedRate;
            }
        } catch (Exception e) {
            log.error("Erro ao consultar taxa de câmbio: {}", e.getMessage());
        }

        if (cachedRate != null) {
            log.warn("Usando taxa de câmbio em cache expirada");
            return cachedRate;
        }

        log.warn("Taxa de câmbio indisponível");
        return null;
    }
}
