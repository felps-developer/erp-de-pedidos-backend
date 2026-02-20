package com.golden.erp.infrastructure.persistence.order;

import com.golden.erp.domain.order.entity.Order;
import com.golden.erp.domain.order.entity.OrderItem;
import com.golden.erp.domain.order.valueobject.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryImplTest {

    @Mock
    private OrderJpaRepository jpaRepository;

    @Mock
    private OrderMapper mapper;

    @InjectMocks
    private OrderRepositoryImpl repository;

    private Order sampleOrder;
    private OrderJpaEntity sampleEntity;

    @BeforeEach
    void setUp() {
        sampleOrder = Order.builder()
                .id(1L).clienteId(1L).clienteNome("João")
                .status(OrderStatus.CREATED)
                .itens(new ArrayList<>(List.of(OrderItem.builder()
                        .id(1L).produtoId(1L).produtoNome("Camiseta")
                        .quantidade(2).precoUnitario(new BigDecimal("49.90"))
                        .desconto(new BigDecimal("5.00")).build())))
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();

        sampleEntity = OrderJpaEntity.builder()
                .id(1L).clienteId(1L).clienteNome("João")
                .status(OrderStatus.CREATED)
                .itens(new ArrayList<>(List.of(OrderItemJpaEntity.builder()
                        .id(1L).produtoId(1L).produtoNome("Camiseta")
                        .quantidade(2).precoUnitario(new BigDecimal("49.90"))
                        .desconto(new BigDecimal("5.00")).build())))
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve salvar pedido")
    void shouldSave() {
        when(mapper.toJpaEntity(sampleOrder)).thenReturn(sampleEntity);
        when(jpaRepository.save(sampleEntity)).thenReturn(sampleEntity);
        when(mapper.toDomain(sampleEntity)).thenReturn(sampleOrder);

        Order result = repository.save(sampleOrder);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve buscar por ID")
    void shouldFindById() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(mapper.toDomain(sampleEntity)).thenReturn(sampleOrder);

        Optional<Order> result = repository.findById(1L);

        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("Deve buscar todos com filtros")
    void shouldFindAllWithFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderJpaEntity> page = new PageImpl<>(List.of(sampleEntity));
        when(jpaRepository.findAllWithFilters(OrderStatus.CREATED, null, pageable)).thenReturn(page);
        when(mapper.toDomain(sampleEntity)).thenReturn(sampleOrder);

        Page<Order> result = repository.findAll(OrderStatus.CREATED, null, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar pedidos por status e data")
    void shouldFindByStatusAndDate() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(48);
        when(jpaRepository.findByStatusAndDataCriacaoBefore(OrderStatus.CREATED, threshold))
                .thenReturn(List.of(sampleEntity));
        when(mapper.toDomain(sampleEntity)).thenReturn(sampleOrder);

        List<Order> result = repository.findByStatusAndDataCriacaoBefore(OrderStatus.CREATED, threshold);

        assertThat(result).hasSize(1);
    }
}
