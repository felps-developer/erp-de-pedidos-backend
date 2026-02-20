package com.golden.erp.presentation.controller;

import com.golden.erp.application.order.dto.CreateOrderRequest;
import com.golden.erp.application.order.dto.OrderResponse;
import com.golden.erp.application.order.dto.OrderUsdResponse;
import com.golden.erp.application.order.service.OrderServiceImpl;
import com.golden.erp.domain.order.valueobject.OrderStatus;
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
@Tag(name = "Pedidos", description = "Gerenciamento de pedidos com cálculo de totais, controle de estoque e status")
public class OrderController {

    private final OrderServiceImpl orderService;

    @PostMapping
    @Operation(summary = "Criar pedido", description = "Cria um pedido para um cliente com lista de itens. Baixa o estoque automaticamente. Rejeita com 422 se estoque insuficiente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente ou produto não encontrado"),
            @ApiResponse(responseCode = "422", description = "Estoque insuficiente")
    })
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Listar pedidos", description = "Lista pedidos com paginação, ordenação e filtros por status e cliente")
    public ResponseEntity<Page<OrderResponse>> findAll(
            @Parameter(description = "Filtrar por status: CREATED, PAID, CANCELLED, LATE") @RequestParam(required = false) OrderStatus status,
            @Parameter(description = "Filtrar por ID do cliente") @RequestParam(required = false) Long clienteId,
            @PageableDefault(sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(orderService.findAll(status, clienteId, pageable));
    }

    @PatchMapping("/{id}/pay")
    @Operation(summary = "Pagar pedido", description = "Altera o status do pedido para PAID. Aceita pedidos com status CREATED ou LATE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido pago com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "422", description = "Status do pedido não permite pagamento")
    })
    public ResponseEntity<OrderResponse> pay(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.pay(id));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancelar pedido", description = "Cancela o pedido e devolve o estoque se ainda não foi pago.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido cancelado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "422", description = "Pedido já pago ou já cancelado")
    })
    public ResponseEntity<OrderResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancel(id));
    }

    @GetMapping("/{id}/usd-total")
    @Operation(summary = "Total em USD", description = "Retorna o total do pedido convertido para dólar americano (USD) usando taxa de câmbio com cache de 1h.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Total em USD calculado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "400", description = "Taxa de câmbio indisponível")
    })
    public ResponseEntity<OrderUsdResponse> getUsdTotal(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getUsdTotal(id));
    }
}
