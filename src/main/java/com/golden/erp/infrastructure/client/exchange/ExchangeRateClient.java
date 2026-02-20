package com.golden.erp.infrastructure.client.exchange;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "exchangeRateClient", url = "${exchange.base-url:https://open.er-api.com/v6}")
public interface ExchangeRateClient {

    @GetMapping("/latest/{base}")
    ExchangeRateResponse getLatestRates(@PathVariable("base") String base);
}
