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

    private final ViaCepClient viaCepClient;

    @Override
    public Address lookup(String cep) {
        String cleanCep = cep.replaceAll("[^0-9]", "");

        if (cleanCep.length() != 8) {
            throw new DomainException("CEP inválido: " + cep);
        }

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
            log.error("Erro ao consultar ViaCEP para o CEP {}: {}", cep, e.getMessage());
            throw new DomainException("Erro ao consultar CEP: " + cep);
        }
    }
}
