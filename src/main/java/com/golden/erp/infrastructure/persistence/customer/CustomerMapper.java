package com.golden.erp.infrastructure.persistence.customer;

import com.golden.erp.domain.customer.entity.Customer;
import com.golden.erp.domain.customer.valueobject.Address;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toDomain(CustomerJpaEntity entity) {
        if (entity == null) return null;

        return Customer.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .email(entity.getEmail())
                .cpf(entity.getCpf())
                .endereco(Address.builder()
                        .logradouro(entity.getLogradouro())
                        .numero(entity.getNumero())
                        .complemento(entity.getComplemento())
                        .bairro(entity.getBairro())
                        .cidade(entity.getCidade())
                        .uf(entity.getUf())
                        .cep(entity.getCep())
                        .build())
                .build();
    }

    public CustomerJpaEntity toJpaEntity(Customer domain) {
        if (domain == null) return null;

        Address addr = domain.getEndereco();
        return CustomerJpaEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .email(domain.getEmail())
                .cpf(domain.getCpf())
                .logradouro(addr != null ? addr.getLogradouro() : null)
                .numero(addr != null ? addr.getNumero() : null)
                .complemento(addr != null ? addr.getComplemento() : null)
                .bairro(addr != null ? addr.getBairro() : null)
                .cidade(addr != null ? addr.getCidade() : null)
                .uf(addr != null ? addr.getUf() : null)
                .cep(addr != null ? addr.getCep() : null)
                .build();
    }
}
