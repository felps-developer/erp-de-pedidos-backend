package com.golden.erp.application.customer.service;

import com.golden.erp.application.customer.dto.AddressRequest;
import com.golden.erp.application.customer.dto.CreateCustomerRequest;
import com.golden.erp.application.customer.dto.CustomerResponse;
import com.golden.erp.application.customer.dto.UpdateCustomerRequest;
import com.golden.erp.application.customer.port.AddressLookupPort;
import com.golden.erp.domain.customer.entity.Customer;
import com.golden.erp.domain.customer.repository.CustomerRepository;
import com.golden.erp.domain.customer.valueobject.Address;
import com.golden.erp.domain.exception.DuplicateFieldException;
import com.golden.erp.domain.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressLookupPort addressLookupPort;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer sampleCustomer;

    @BeforeEach
    void setUp() {
        sampleCustomer = Customer.builder()
                .id(1L)
                .nome("João Silva")
                .email("joao@email.com")
                .cpf("12345678901")
                .endereco(Address.builder()
                        .logradouro("Rua A")
                        .numero("100")
                        .bairro("Centro")
                        .cidade("São Paulo")
                        .uf("SP")
                        .cep("01001000")
                        .build())
                .build();
    }

    @Nested
    @DisplayName("Criar Cliente")
    class CreateCustomer {

        @Test
        @DisplayName("Deve criar cliente com sucesso")
        void shouldCreateCustomer() {
            CreateCustomerRequest request = CreateCustomerRequest.builder()
                    .nome("João Silva")
                    .email("joao@email.com")
                    .cpf("12345678901")
                    .endereco(AddressRequest.builder()
                            .logradouro("Rua A")
                            .numero("100")
                            .bairro("Centro")
                            .cidade("São Paulo")
                            .uf("SP")
                            .cep("01001000")
                            .build())
                    .build();

            when(customerRepository.existsByEmail(anyString())).thenReturn(false);
            when(customerRepository.existsByCpf(anyString())).thenReturn(false);
            when(customerRepository.save(any(Customer.class))).thenReturn(sampleCustomer);

            CustomerResponse response = customerService.create(request);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getNome()).isEqualTo("João Silva");
            assertThat(response.getEmail()).isEqualTo("joao@email.com");
            verify(customerRepository).save(any(Customer.class));
        }

        @Test
        @DisplayName("Deve enriquecer endereço via CEP quando campos estão vazios")
        void shouldEnrichAddressViaCep() {
            CreateCustomerRequest request = CreateCustomerRequest.builder()
                    .nome("João Silva")
                    .email("joao@email.com")
                    .cpf("12345678901")
                    .endereco(AddressRequest.builder()
                            .numero("100")
                            .cep("01001000")
                            .build())
                    .build();

            Address viaCepAddress = Address.builder()
                    .logradouro("Praça da Sé")
                    .bairro("Sé")
                    .cidade("São Paulo")
                    .uf("SP")
                    .cep("01001000")
                    .build();

            when(customerRepository.existsByEmail(anyString())).thenReturn(false);
            when(customerRepository.existsByCpf(anyString())).thenReturn(false);
            when(addressLookupPort.lookup("01001000")).thenReturn(viaCepAddress);
            when(customerRepository.save(any(Customer.class))).thenReturn(sampleCustomer);

            customerService.create(request);

            verify(addressLookupPort).lookup("01001000");
            verify(customerRepository).save(any(Customer.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando email já existe")
        void shouldThrowWhenEmailExists() {
            CreateCustomerRequest request = CreateCustomerRequest.builder()
                    .nome("João Silva")
                    .email("joao@email.com")
                    .cpf("12345678901")
                    .build();

            when(customerRepository.existsByEmail("joao@email.com")).thenReturn(true);

            assertThatThrownBy(() -> customerService.create(request))
                    .isInstanceOf(DuplicateFieldException.class)
                    .hasMessageContaining("email");
        }

        @Test
        @DisplayName("Deve lançar exceção quando CPF já existe")
        void shouldThrowWhenCpfExists() {
            CreateCustomerRequest request = CreateCustomerRequest.builder()
                    .nome("João Silva")
                    .email("joao@email.com")
                    .cpf("12345678901")
                    .build();

            when(customerRepository.existsByEmail(anyString())).thenReturn(false);
            when(customerRepository.existsByCpf("12345678901")).thenReturn(true);

            assertThatThrownBy(() -> customerService.create(request))
                    .isInstanceOf(DuplicateFieldException.class)
                    .hasMessageContaining("cpf");
        }
    }

    @Nested
    @DisplayName("Buscar Cliente")
    class FindCustomer {

        @Test
        @DisplayName("Deve retornar cliente por ID")
        void shouldFindById() {
            when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer));

            CustomerResponse response = customerService.findById(1L);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getNome()).isEqualTo("João Silva");
        }

        @Test
        @DisplayName("Deve lançar exceção quando cliente não encontrado")
        void shouldThrowWhenNotFound() {
            when(customerRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.findById(99L))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("Deve retornar lista paginada de clientes")
        void shouldFindAllPaginated() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Customer> page = new PageImpl<>(List.of(sampleCustomer));
            when(customerRepository.findAll(null, null, pageable)).thenReturn(page);

            Page<CustomerResponse> result = customerService.findAll(null, null, pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getNome()).isEqualTo("João Silva");
        }
    }

    @Nested
    @DisplayName("Atualizar Cliente")
    class UpdateCustomer {

        @Test
        @DisplayName("Deve atualizar cliente com sucesso")
        void shouldUpdateCustomer() {
            UpdateCustomerRequest request = UpdateCustomerRequest.builder()
                    .nome("João Atualizado")
                    .email("joao@email.com")
                    .cpf("12345678901")
                    .build();

            Customer updatedCustomer = Customer.builder()
                    .id(1L)
                    .nome("João Atualizado")
                    .email("joao@email.com")
                    .cpf("12345678901")
                    .build();

            when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer));
            when(customerRepository.existsByEmailAndIdNot("joao@email.com", 1L)).thenReturn(false);
            when(customerRepository.existsByCpfAndIdNot("12345678901", 1L)).thenReturn(false);
            when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

            CustomerResponse response = customerService.update(1L, request);

            assertThat(response.getNome()).isEqualTo("João Atualizado");
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar cliente inexistente")
        void shouldThrowWhenUpdatingNonExistent() {
            UpdateCustomerRequest request = UpdateCustomerRequest.builder()
                    .nome("Test")
                    .email("test@email.com")
                    .cpf("12345678901")
                    .build();

            when(customerRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.update(99L, request))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Deletar Cliente")
    class DeleteCustomer {

        @Test
        @DisplayName("Deve deletar cliente com sucesso")
        void shouldDeleteCustomer() {
            when(customerRepository.existsById(1L)).thenReturn(true);

            customerService.delete(1L);

            verify(customerRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Deve lançar exceção ao deletar cliente inexistente")
        void shouldThrowWhenDeletingNonExistent() {
            when(customerRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> customerService.delete(99L))
                    .isInstanceOf(EntityNotFoundException.class);

            verify(customerRepository, never()).deleteById(any());
        }
    }
}
