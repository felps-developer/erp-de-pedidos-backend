package com.golden.erp.domain.order.repository;

import com.golden.erp.domain.order.entity.Order;
import com.golden.erp.domain.order.valueobject.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long id);

    Page<Order> findAll(OrderStatus status, Long clienteId, Pageable pageable);

    List<Order> findByStatusAndDataCriacaoBefore(OrderStatus status, LocalDateTime dateTime);
}
