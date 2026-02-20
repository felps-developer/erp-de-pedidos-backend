package com.golden.erp.infrastructure.client.exchange;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateAdapterTest {

    @Mock
    private ExchangeRateClient exchangeRateClient;

    @InjectMocks
    private ExchangeRateAdapter adapter;

    @Test
    @DisplayName("Deve retornar taxa quando API retorna dados válidos")
    void shouldReturnRateWhenApiReturnsValid() {
        ExchangeRateResponse response = new ExchangeRateResponse("success", Map.of("USD", new BigDecimal("0.191176")));
        when(exchangeRateClient.getLatestRates("BRL")).thenReturn(response);

        BigDecimal rate = adapter.getBrlToUsdRate();

        assertThat(rate).isEqualByComparingTo(new BigDecimal("0.191176"));
    }

    @Test
    @DisplayName("Deve usar cache na segunda chamada")
    void shouldUseCacheOnSecondCall() {
        ExchangeRateResponse response = new ExchangeRateResponse("success", Map.of("USD", new BigDecimal("0.200000")));
        when(exchangeRateClient.getLatestRates("BRL")).thenReturn(response);

        adapter.getBrlToUsdRate();
        BigDecimal secondCall = adapter.getBrlToUsdRate();

        assertThat(secondCall).isEqualByComparingTo(new BigDecimal("0.200000"));
        verify(exchangeRateClient, times(1)).getLatestRates("BRL");
    }

    @Test
    @DisplayName("Deve retornar null quando API falha e não há cache")
    void shouldReturnNullWhenApiFailsAndNoCache() {
        when(exchangeRateClient.getLatestRates("BRL")).thenThrow(new RuntimeException("Timeout"));

        BigDecimal rate = adapter.getBrlToUsdRate();

        assertThat(rate).isNull();
    }

    @Test
    @DisplayName("Deve retornar null quando API retorna resposta sem rates")
    void shouldReturnNullWhenNoRates() {
        ExchangeRateResponse response = new ExchangeRateResponse("success", null);
        when(exchangeRateClient.getLatestRates("BRL")).thenReturn(response);

        BigDecimal rate = adapter.getBrlToUsdRate();

        assertThat(rate).isNull();
    }

    @Test
    @DisplayName("Deve retornar null quando API retorna null")
    void shouldReturnNullWhenResponseIsNull() {
        when(exchangeRateClient.getLatestRates("BRL")).thenReturn(null);

        BigDecimal rate = adapter.getBrlToUsdRate();

        assertThat(rate).isNull();
    }

    @Test
    @DisplayName("Deve retornar null quando rates não contém USD")
    void shouldReturnNullWhenNoUsdKey() {
        ExchangeRateResponse response = new ExchangeRateResponse("success", Map.of("EUR", new BigDecimal("0.85")));
        when(exchangeRateClient.getLatestRates("BRL")).thenReturn(response);

        BigDecimal rate = adapter.getBrlToUsdRate();

        assertThat(rate).isNull();
    }
}
