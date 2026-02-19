package com.golden.erp.presentation.controller;

import com.golden.erp.application.customer.dto.CreateCustomerRequest;
import com.golden.erp.application.customer.dto.CustomerResponse;
import com.golden.erp.application.customer.dto.UpdateCustomerRequest;
import com.golden.erp.application.customer.service.CustomerServiceImpl;
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
public class CustomerController {

    private final CustomerServiceImpl customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest request) {
        CustomerResponse response = customerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> findAll(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @PageableDefault(sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(customerService.findAll(nome, email, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCustomerRequest request) {
        return ResponseEntity.ok(customerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
