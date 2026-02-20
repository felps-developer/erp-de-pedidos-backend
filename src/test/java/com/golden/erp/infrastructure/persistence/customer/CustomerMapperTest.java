package com.golden.erp.infrastructure.persistence.customer;

import com.golden.erp.domain.customer.entity.Customer;
import com.golden.erp.domain.customer.valueobject.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerMapperTest {

    private final CustomerMapper mapper = new CustomerMapper();

    @Test
    @DisplayName("Deve converter JpaEntity para Domain")
    void shouldMapToDomain() {
        CustomerJpaEntity entity = CustomerJpaEntity.builder()
                .id(1L).nome("Maria").email("maria@email.com").cpf("12345678901")
                .logradouro("Rua A").numero("100").complemento("Apto 1")
                .bairro("Centro").cidade("Recife").uf("PE").cep("50000000")
                .build();

        Customer result = mapper.toDomain(entity);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNome()).isEqualTo("Maria");
        assertThat(result.getEmail()).isEqualTo("maria@email.com");
        assertThat(result.getCpf()).isEqualTo("12345678901");
        assertThat(result.getEndereco().getLogradouro()).isEqualTo("Rua A");
        assertThat(result.getEndereco().getNumero()).isEqualTo("100");
        assertThat(result.getEndereco().getComplemento()).isEqualTo("Apto 1");
        assertThat(result.getEndereco().getBairro()).isEqualTo("Centro");
        assertThat(result.getEndereco().getCidade()).isEqualTo("Recife");
        assertThat(result.getEndereco().getUf()).isEqualTo("PE");
        assertThat(result.getEndereco().getCep()).isEqualTo("50000000");
    }

    @Test
    @DisplayName("Deve retornar null quando entity eh null")
    void shouldReturnNullWhenEntityNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("Deve converter Domain para JpaEntity")
    void shouldMapToJpaEntity() {
        Customer customer = Customer.builder()
                .id(1L).nome("Maria").email("maria@email.com").cpf("12345678901")
                .endereco(Address.builder()
                        .logradouro("Rua A").numero("100").complemento("Apto 1")
                        .bairro("Centro").cidade("Recife").uf("PE").cep("50000000")
                        .build())
                .build();

        CustomerJpaEntity result = mapper.toJpaEntity(customer);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNome()).isEqualTo("Maria");
        assertThat(result.getLogradouro()).isEqualTo("Rua A");
        assertThat(result.getCep()).isEqualTo("50000000");
    }

    @Test
    @DisplayName("Deve retornar null quando domain eh null")
    void shouldReturnNullWhenDomainNull() {
        assertThat(mapper.toJpaEntity(null)).isNull();
    }

    @Test
    @DisplayName("Deve converter domain sem endereco")
    void shouldMapWithoutAddress() {
        Customer customer = Customer.builder()
                .id(1L).nome("Maria").email("maria@email.com").cpf("12345678901")
                .endereco(null)
                .build();

        CustomerJpaEntity result = mapper.toJpaEntity(customer);

        assertThat(result.getLogradouro()).isNull();
        assertThat(result.getCep()).isNull();
    }
}
