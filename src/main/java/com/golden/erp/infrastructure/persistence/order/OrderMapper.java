package com.golden.erp.infrastructure.persistence.order;

import com.golden.erp.domain.order.entity.Order;
import com.golden.erp.domain.order.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderMapper {

    public Order toDomain(OrderJpaEntity entity) {
        if (entity == null) return null;

        List<OrderItem> itens = entity.getItens().stream()
                .map(this::toItemDomain)
                .toList();

        return Order.builder()
                .id(entity.getId())
                .clienteId(entity.getClienteId())
                .clienteNome(entity.getClienteNome())
                .status(entity.getStatus())
                .itens(new ArrayList<>(itens))
                .dataCriacao(entity.getDataCriacao())
                .dataAtualizacao(entity.getDataAtualizacao())
                .build();
    }

    public OrderJpaEntity toJpaEntity(Order domain) {
        if (domain == null) return null;

        OrderJpaEntity orderEntity = OrderJpaEntity.builder()
                .id(domain.getId())
                .clienteId(domain.getClienteId())
                .clienteNome(domain.getClienteNome())
                .status(domain.getStatus())
                .dataCriacao(domain.getDataCriacao())
                .dataAtualizacao(domain.getDataAtualizacao())
                .itens(new ArrayList<>())
                .build();

        if (domain.getItens() != null) {
            List<OrderItemJpaEntity> jpaItems = domain.getItens().stream()
                    .map(item -> toItemJpaEntity(item, orderEntity))
                    .toList();
            orderEntity.getItens().addAll(jpaItems);
        }

        return orderEntity;
    }

    private OrderItem toItemDomain(OrderItemJpaEntity entity) {
        return OrderItem.builder()
                .id(entity.getId())
                .produtoId(entity.getProdutoId())
                .produtoNome(entity.getProdutoNome())
                .quantidade(entity.getQuantidade())
                .precoUnitario(entity.getPrecoUnitario())
                .desconto(entity.getDesconto())
                .build();
    }

    private OrderItemJpaEntity toItemJpaEntity(OrderItem domain, OrderJpaEntity order) {
        return OrderItemJpaEntity.builder()
                .id(domain.getId())
                .order(order)
                .produtoId(domain.getProdutoId())
                .produtoNome(domain.getProdutoNome())
                .quantidade(domain.getQuantidade())
                .precoUnitario(domain.getPrecoUnitario())
                .desconto(domain.getDesconto())
                .build();
    }
}
