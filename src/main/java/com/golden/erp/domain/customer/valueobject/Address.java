package com.golden.erp.domain.customer.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;

    public boolean needsEnrichment() {
        return cep != null && !cep.isBlank()
                && (isBlank(logradouro) || isBlank(bairro) || isBlank(cidade) || isBlank(uf));
    }

    public Address enrichWith(String logradouro, String bairro, String cidade, String uf) {
        return Address.builder()
                .logradouro(isBlank(this.logradouro) ? logradouro : this.logradouro)
                .numero(this.numero)
                .complemento(this.complemento)
                .bairro(isBlank(this.bairro) ? bairro : this.bairro)
                .cidade(isBlank(this.cidade) ? cidade : this.cidade)
                .uf(isBlank(this.uf) ? uf : this.uf)
                .cep(this.cep)
                .build();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
