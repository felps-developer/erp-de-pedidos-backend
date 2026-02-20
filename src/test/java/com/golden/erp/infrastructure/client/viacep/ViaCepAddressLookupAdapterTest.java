package com.golden.erp.infrastructure.client.viacep;

import com.golden.erp.domain.customer.valueobject.Address;
import com.golden.erp.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ViaCepAddressLookupAdapterTest {

    @Mock
    private ViaCepClient viaCepClient;

    @InjectMocks
    private ViaCepAddressLookupAdapter adapter;

    @Test
    @DisplayName("Deve retornar endereço quando CEP é válido")
    void shouldReturnAddressForValidCep() {
        ViaCepResponse response = new ViaCepResponse("01001-000", "Praça da Sé", "lado ímpar", "Sé", "São Paulo", "SP", null);
        when(viaCepClient.findByCep("01001000")).thenReturn(response);

        Address result = adapter.lookup("01001-000");

        assertThat(result.getLogradouro()).isEqualTo("Praça da Sé");
        assertThat(result.getBairro()).isEqualTo("Sé");
        assertThat(result.getCidade()).isEqualTo("São Paulo");
        assertThat(result.getUf()).isEqualTo("SP");
        assertThat(result.getCep()).isEqualTo("01001000");
    }

    @Test
    @DisplayName("Deve lançar exceção para CEP com menos de 8 dígitos")
    void shouldThrowForShortCep() {
        assertThatThrownBy(() -> adapter.lookup("1234"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("CEP inválido");
    }

    @Test
    @DisplayName("Deve lançar exceção quando ViaCEP retorna erro")
    void shouldThrowWhenViaCepReturnsError() {
        ViaCepResponse response = new ViaCepResponse(null, null, null, null, null, null, true);
        when(viaCepClient.findByCep("99999999")).thenReturn(response);

        assertThatThrownBy(() -> adapter.lookup("99999999"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("CEP não encontrado");
    }

    @Test
    @DisplayName("Deve lançar exceção quando ViaCEP retorna null")
    void shouldThrowWhenViaCepReturnsNull() {
        when(viaCepClient.findByCep("99999999")).thenReturn(null);

        assertThatThrownBy(() -> adapter.lookup("99999999"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("CEP não encontrado");
    }

    @Test
    @DisplayName("Deve lançar exceção após todas as tentativas falharem por erro de rede")
    void shouldThrowAfterAllRetriesFail() {
        when(viaCepClient.findByCep("01001000")).thenThrow(new RuntimeException("Timeout"));

        assertThatThrownBy(() -> adapter.lookup("01001000"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("tentativas");
    }

    @Test
    @DisplayName("Deve limpar caracteres não numéricos do CEP")
    void shouldCleanCepBeforeLookup() {
        ViaCepResponse response = new ViaCepResponse("01001-000", "Praça da Sé", null, "Sé", "São Paulo", "SP", null);
        when(viaCepClient.findByCep("01001000")).thenReturn(response);

        Address result = adapter.lookup("01.001-000");

        assertThat(result.getCep()).isEqualTo("01001000");
    }
}
