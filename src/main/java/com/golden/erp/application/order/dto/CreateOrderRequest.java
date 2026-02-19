package com.golden.erp.application.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotNull(message = "ID do cliente é obrigatório")
    private Long clienteId;

    @NotEmpty(message = "O pedido deve ter pelo menos um item")
    @Valid
    private List<OrderItemRequest> itens;
}
