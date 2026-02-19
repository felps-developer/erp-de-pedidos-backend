package com.golden.erp.infrastructure.client.viacep;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "viaCepClient", url = "${viacep.base-url}")
public interface ViaCepClient {

    @GetMapping("/{cep}/json/")
    ViaCepResponse findByCep(@PathVariable("cep") String cep);
}
