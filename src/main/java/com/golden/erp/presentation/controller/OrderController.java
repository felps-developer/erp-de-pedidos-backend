package com.golden.erp.presentation.controller;

import com.golden.erp.application.order.dto.CreateOrderRequest;
import com.golden.erp.application.order.dto.OrderResponse;
import com.golden.erp.application.order.dto.OrderUsdResponse;
import com.golden.erp.application.order.service.OrderServiceImpl;
import com.golden.erp.domain.order.valueobject.OrderStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> findAll(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Long clienteId,
            @PageableDefault(sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(orderService.findAll(status, clienteId, pageable));
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<OrderResponse> pay(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.pay(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancel(id));
    }

    @GetMapping("/{id}/usd-total")
    public ResponseEntity<OrderUsdResponse> getUsdTotal(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getUsdTotal(id));
    }
}
