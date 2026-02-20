package com.golden.erp.infrastructure.persistence.order;

import com.golden.erp.domain.order.entity.Order;
import com.golden.erp.domain.order.entity.OrderItem;
import com.golden.erp.domain.order.valueobject.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMapperTest {

    private final OrderMapper mapper = new OrderMapper();

    @Test
    @DisplayName("Deve converter JpaEntity para Domain")
    void shouldMapToDomain() {
        OrderJpaEntity orderEntity = OrderJpaEntity.builder()
                .id(1L).clienteId(10L).clienteNome("Cliente")
                .status(OrderStatus.CREATED)
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .itens(new ArrayList<>())
                .build();

        OrderItemJpaEntity itemEntity = OrderItemJpaEntity.builder()
                .id(1L).order(orderEntity)
                .produtoId(5L).produtoNome("Produto")
                .quantidade(3).precoUnitario(new BigDecimal("25.00"))
                .desconto(new BigDecimal("2.50"))
                .build();
        orderEntity.getItens().add(itemEntity);

        Order result = mapper.toDomain(orderEntity);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getClienteId()).isEqualTo(10L);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(result.getItens()).hasSize(1);
        assertThat(result.getItens().get(0).getProdutoNome()).isEqualTo("Produto");
    }

    @Test
    @DisplayName("Deve retornar null quando entity eh null")
    void shouldReturnNullWhenEntityNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("Deve converter Domain para JpaEntity")
    void shouldMapToJpaEntity() {
        Order order = Order.builder()
                .id(1L).clienteId(10L).clienteNome("Cliente")
                .status(OrderStatus.CREATED)
                .itens(new ArrayList<>(List.of(
                        OrderItem.builder()
                                .id(1L).produtoId(5L).produtoNome("Produto")
                                .quantidade(3).precoUnitario(new BigDecimal("25.00"))
                                .desconto(new BigDecimal("2.50"))
                                .build())))
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();

        OrderJpaEntity result = mapper.toJpaEntity(order);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getClienteId()).isEqualTo(10L);
        assertThat(result.getItens()).hasSize(1);
        assertThat(result.getItens().get(0).getProdutoNome()).isEqualTo("Produto");
        assertThat(result.getItens().get(0).getOrder()).isEqualTo(result);
    }

    @Test
    @DisplayName("Deve retornar null quando domain eh null")
    void shouldReturnNullWhenDomainNull() {
        assertThat(mapper.toJpaEntity(null)).isNull();
    }

    @Test
    @DisplayName("Deve converter domain sem itens")
    void shouldMapWithNullItens() {
        Order order = Order.builder()
                .id(1L).clienteId(10L).clienteNome("Cliente")
                .status(OrderStatus.CREATED)
                .itens(null)
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();

        OrderJpaEntity result = mapper.toJpaEntity(order);

        assertThat(result.getItens()).isEmpty();
    }
}
