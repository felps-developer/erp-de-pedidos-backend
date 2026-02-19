package com.golden.erp.domain.customer.entity;

import com.golden.erp.domain.customer.valueobject.Address;
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
public class Customer {

    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private Address endereco;
}
