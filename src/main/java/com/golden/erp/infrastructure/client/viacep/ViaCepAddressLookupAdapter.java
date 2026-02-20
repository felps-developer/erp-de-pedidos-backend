package com.golden.erp.infrastructure.client.viacep;

import com.golden.erp.application.customer.port.AddressLookupPort;
import com.golden.erp.domain.customer.valueobject.Address;
import com.golden.erp.domain.exception.DomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViaCepAddressLookupAdapter implements AddressLookupPort {

    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_BACKOFF_MS = 500;

    private final ViaCepClient viaCepClient;

    @Override
    public Address lookup(String cep) {
        String cleanCep = cep.replaceAll("[^0-9]", "");

        if (cleanCep.length() != 8) {
            throw new DomainException("CEP inválido: " + cep);
        }

        Exception lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                ViaCepResponse response = viaCepClient.findByCep(cleanCep);

                if (response == null || Boolean.TRUE.equals(response.getErro())) {
                    throw new DomainException("CEP não encontrado: " + cep);
                }

                return Address.builder()
                        .logradouro(response.getLogradouro())
                        .bairro(response.getBairro())
                        .cidade(response.getLocalidade())
                        .uf(response.getUf())
                        .cep(cleanCep)
                        .build();
            } catch (DomainException e) {
                throw e;
            } catch (Exception e) {
                lastException = e;
                log.warn("Tentativa {}/{} falhou ao consultar ViaCEP para CEP {}: {}",
                        attempt, MAX_RETRIES, cep, e.getMessage());

                if (attempt < MAX_RETRIES) {
                    try {
                        long sleepMs = INITIAL_BACKOFF_MS * (1L << (attempt - 1));
                        Thread.sleep(sleepMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        log.error("Todas as {} tentativas falharam ao consultar ViaCEP para CEP {}", MAX_RETRIES, cep);
        throw new DomainException("Erro ao consultar CEP após " + MAX_RETRIES + " tentativas: " + cep
                + " - " + (lastException != null ? lastException.getMessage() : "erro desconhecido"));
    }
}
