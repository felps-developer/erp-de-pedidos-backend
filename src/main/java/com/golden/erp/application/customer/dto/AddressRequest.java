package com.golden.erp.application.customer.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {

    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;

    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP deve ter o formato 00000-000 ou 00000000")
    private String cep;
}
