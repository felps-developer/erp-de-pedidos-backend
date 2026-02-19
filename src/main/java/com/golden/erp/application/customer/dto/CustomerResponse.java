package com.golden.erp.application.customer.dto;

import com.golden.erp.domain.customer.entity.Customer;
import com.golden.erp.domain.customer.valueobject.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private AddressResponse endereco;

    public static CustomerResponse from(Customer customer) {
        Address addr = customer.getEndereco();
        return CustomerResponse.builder()
                .id(customer.getId())
                .nome(customer.getNome())
                .email(customer.getEmail())
                .cpf(customer.getCpf())
                .endereco(addr != null ? AddressResponse.builder()
                        .logradouro(addr.getLogradouro())
                        .numero(addr.getNumero())
                        .complemento(addr.getComplemento())
                        .bairro(addr.getBairro())
                        .cidade(addr.getCidade())
                        .uf(addr.getUf())
                        .cep(addr.getCep())
                        .build() : null)
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressResponse {
        private String logradouro;
        private String numero;
        private String complemento;
        private String bairro;
        private String cidade;
        private String uf;
        private String cep;
    }
}
