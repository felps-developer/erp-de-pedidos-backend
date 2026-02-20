package com.golden.erp.application.scheduler;

import com.golden.erp.domain.order.entity.Order;
import com.golden.erp.domain.order.entity.OrderItem;
import com.golden.erp.domain.order.repository.OrderRepository;
import com.golden.erp.domain.order.valueobject.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LateOrderSchedulerTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private LateOrderScheduler scheduler;

    @Test
    @DisplayName("Deve marcar pedidos atrasados como LATE")
    void shouldMarkLateOrders() {
        Order lateOrder = Order.builder()
                .id(1L)
                .clienteId(1L)
                .clienteNome("Test")
                .status(OrderStatus.CREATED)
                .itens(new ArrayList<>(List.of(OrderItem.builder()
                        .produtoId(1L).produtoNome("P").quantidade(1)
                        .precoUnitario(new BigDecimal("10.00")).build())))
                .dataCriacao(LocalDateTime.now().minusHours(50))
                .dataAtualizacao(LocalDateTime.now().minusHours(50))
                .build();

        when(orderRepository.findByStatusAndDataCriacaoBefore(eq(OrderStatus.CREATED), any(LocalDateTime.class)))
                .thenReturn(List.of(lateOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(lateOrder);

        scheduler.markLateOrders();

        assertThat(lateOrder.getStatus()).isEqualTo(OrderStatus.LATE);
        verify(orderRepository).save(lateOrder);
    }

    @Test
    @DisplayName("Não deve fazer nada quando não há pedidos atrasados")
    void shouldDoNothingWhenNoLateOrders() {
        when(orderRepository.findByStatusAndDataCriacaoBefore(eq(OrderStatus.CREATED), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        scheduler.markLateOrders();

        verify(orderRepository, never()).save(any());
    }
}
