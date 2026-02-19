package com.golden.erp.infrastructure.client.exchange;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "exchangeRateClient", url = "${exchange.base-url:https://api.exchangerate.host}")
public interface ExchangeRateClient {

    @GetMapping("/latest")
    ExchangeRateResponse getLatestRates(@RequestParam("base") String base,
                                         @RequestParam("symbols") String symbols);
}
