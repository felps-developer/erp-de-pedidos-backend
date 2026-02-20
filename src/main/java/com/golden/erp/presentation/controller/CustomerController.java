package com.golden.erp.presentation.controller;

import com.golden.erp.application.customer.dto.CreateCustomerRequest;
import com.golden.erp.application.customer.dto.CustomerResponse;
import com.golden.erp.application.customer.dto.UpdateCustomerRequest;
import com.golden.erp.application.customer.service.CustomerServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gerenciamento de clientes com enriquecimento automático de endereço via CEP")
public class CustomerController {

    private final CustomerServiceImpl customerService;

    @PostMapping
    @Operation(summary = "Criar cliente", description = "Cria um novo cliente. Se o CEP for informado sem logradouro/bairro/cidade/uf, o endereço é enriquecido automaticamente via ViaCEP.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Email ou CPF já cadastrado")
    })
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest request) {
        CustomerResponse response = customerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<CustomerResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Lista clientes com paginação, ordenação e filtros por nome e email")
    public ResponseEntity<Page<CustomerResponse>> findAll(
            @Parameter(description = "Filtrar por nome (parcial, case-insensitive)") @RequestParam(required = false) String nome,
            @Parameter(description = "Filtrar por email (parcial, case-insensitive)") @RequestParam(required = false) String email,
            @PageableDefault(sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(customerService.findAll(nome, email, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente atualizado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "409", description = "Email ou CPF já cadastrado por outro cliente")
    })
    public ResponseEntity<CustomerResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCustomerRequest request) {
        return ResponseEntity.ok(customerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente removido"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
