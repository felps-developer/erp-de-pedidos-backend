package com.golden.erp.application.order.dto;

import com.golden.erp.domain.order.entity.Order;
import com.golden.erp.domain.order.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private Long clienteId;
    private String clienteNome;
    private String status;
    private List<OrderItemResponse> itens;
    private BigDecimal subtotal;
    private BigDecimal descontos;
    private BigDecimal total;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public static OrderResponse from(Order order) {
        List<OrderItemResponse> itens = order.getItens().stream()
                .map(OrderItemResponse::from)
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .clienteId(order.getClienteId())
                .clienteNome(order.getClienteNome())
                .status(order.getStatus().name())
                .itens(itens)
                .subtotal(order.getSubtotal())
                .descontos(order.getDescontos())
                .total(order.getTotal())
                .dataCriacao(order.getDataCriacao())
                .dataAtualizacao(order.getDataAtualizacao())
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {

        private Long id;
        private Long produtoId;
        private String produtoNome;
        private Integer quantidade;
        private BigDecimal precoUnitario;
        private BigDecimal desconto;
        private BigDecimal subtotal;
        private BigDecimal total;

        public static OrderItemResponse from(OrderItem item) {
            return OrderItemResponse.builder()
                    .id(item.getId())
                    .produtoId(item.getProdutoId())
                    .produtoNome(item.getProdutoNome())
                    .quantidade(item.getQuantidade())
                    .precoUnitario(item.getPrecoUnitario())
                    .desconto(item.getDesconto())
                    .subtotal(item.getSubtotal())
                    .total(item.getTotal())
                    .build();
        }
    }
}
