package com.golden.erp.presentation.handler;

import com.golden.erp.domain.exception.DomainException;
import com.golden.erp.domain.exception.DuplicateFieldException;
import com.golden.erp.domain.exception.EntityNotFoundException;
import com.golden.erp.domain.exception.InsufficientStockException;
import com.golden.erp.domain.exception.InvalidOrderStateException;
import com.golden.erp.presentation.dto.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Deve retornar 404 para EntityNotFoundException")
    void shouldReturn404ForNotFound() {
        EntityNotFoundException ex = new EntityNotFoundException("Cliente", 1L);

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
    }

    @Test
    @DisplayName("Deve retornar 409 para DuplicateFieldException")
    void shouldReturn409ForDuplicate() {
        DuplicateFieldException ex = new DuplicateFieldException("email", "test@test.com");

        ResponseEntity<ErrorResponse> response = handler.handleDuplicate(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
    }

    @Test
    @DisplayName("Deve retornar 422 para InsufficientStockException")
    void shouldReturn422ForInsufficientStock() {
        InsufficientStockException ex = new InsufficientStockException("Camiseta", 5, 2);

        ResponseEntity<ErrorResponse> response = handler.handleInsufficientStock(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(422);
    }

    @Test
    @DisplayName("Deve retornar 422 para InvalidOrderStateException")
    void shouldReturn422ForInvalidOrderState() {
        InvalidOrderStateException ex = new InvalidOrderStateException("CANCELLED", "pay");

        ResponseEntity<ErrorResponse> response = handler.handleInvalidOrderState(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(422);
    }

    @Test
    @DisplayName("Deve retornar 400 para DomainException")
    void shouldReturn400ForDomainException() {
        DomainException ex = new DomainException("CEP inválido");

        ResponseEntity<ErrorResponse> response = handler.handleDomain(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).isEqualTo("CEP inválido");
    }

    @Test
    @DisplayName("Deve retornar 400 para erros de validação")
    void shouldReturn400ForValidation() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "nome", "Nome é obrigatório"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrors()).containsKey("nome");
    }

    @Test
    @DisplayName("Deve retornar 500 para exceções genéricas")
    void shouldReturn500ForGenericException() {
        Exception ex = new RuntimeException("Erro inesperado");

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
    }
}
