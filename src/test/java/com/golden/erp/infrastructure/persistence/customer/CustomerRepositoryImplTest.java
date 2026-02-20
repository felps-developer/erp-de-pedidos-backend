package com.golden.erp.infrastructure.persistence.customer;

import com.golden.erp.domain.customer.entity.Customer;
import com.golden.erp.domain.customer.valueobject.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerRepositoryImplTest {

    @Mock
    private CustomerJpaRepository jpaRepository;

    @Mock
    private CustomerMapper mapper;

    @InjectMocks
    private CustomerRepositoryImpl repository;

    private Customer sampleCustomer;
    private CustomerJpaEntity sampleEntity;

    @BeforeEach
    void setUp() {
        sampleCustomer = Customer.builder()
                .id(1L).nome("João").email("joao@email.com").cpf("12345678901")
                .endereco(Address.builder().cep("01001000").numero("100").build())
                .build();

        sampleEntity = CustomerJpaEntity.builder()
                .id(1L).nome("João").email("joao@email.com").cpf("12345678901")
                .cep("01001000").numero("100")
                .build();
    }

    @Test
    @DisplayName("Deve salvar cliente")
    void shouldSave() {
        when(mapper.toJpaEntity(sampleCustomer)).thenReturn(sampleEntity);
        when(jpaRepository.save(sampleEntity)).thenReturn(sampleEntity);
        when(mapper.toDomain(sampleEntity)).thenReturn(sampleCustomer);

        Customer result = repository.save(sampleCustomer);

        assertThat(result.getId()).isEqualTo(1L);
        verify(jpaRepository).save(sampleEntity);
    }

    @Test
    @DisplayName("Deve buscar por ID")
    void shouldFindById() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(mapper.toDomain(sampleEntity)).thenReturn(sampleCustomer);

        Optional<Customer> result = repository.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getNome()).isEqualTo("João");
    }

    @Test
    @DisplayName("Deve retornar vazio quando não encontrado")
    void shouldReturnEmptyWhenNotFound() {
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Customer> result = repository.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar todos com filtros")
    void shouldFindAllWithFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CustomerJpaEntity> page = new PageImpl<>(List.of(sampleEntity));
        when(jpaRepository.findAllWithFilters("João", null, pageable)).thenReturn(page);
        when(mapper.toDomain(sampleEntity)).thenReturn(sampleCustomer);

        Page<Customer> result = repository.findAll("João", null, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve verificar existência por email")
    void shouldCheckExistsByEmail() {
        when(jpaRepository.existsByEmail("joao@email.com")).thenReturn(true);
        assertThat(repository.existsByEmail("joao@email.com")).isTrue();
    }

    @Test
    @DisplayName("Deve verificar existência por CPF")
    void shouldCheckExistsByCpf() {
        when(jpaRepository.existsByCpf("12345678901")).thenReturn(true);
        assertThat(repository.existsByCpf("12345678901")).isTrue();
    }

    @Test
    @DisplayName("Deve verificar existência por email excluindo ID")
    void shouldCheckExistsByEmailAndIdNot() {
        when(jpaRepository.existsByEmailAndIdNot("joao@email.com", 1L)).thenReturn(false);
        assertThat(repository.existsByEmailAndIdNot("joao@email.com", 1L)).isFalse();
    }

    @Test
    @DisplayName("Deve verificar existência por CPF excluindo ID")
    void shouldCheckExistsByCpfAndIdNot() {
        when(jpaRepository.existsByCpfAndIdNot("12345678901", 1L)).thenReturn(false);
        assertThat(repository.existsByCpfAndIdNot("12345678901", 1L)).isFalse();
    }

    @Test
    @DisplayName("Deve deletar por ID")
    void shouldDeleteById() {
        repository.deleteById(1L);
        verify(jpaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve verificar existência por ID")
    void shouldCheckExistsById() {
        when(jpaRepository.existsById(1L)).thenReturn(true);
        assertThat(repository.existsById(1L)).isTrue();
    }
}
