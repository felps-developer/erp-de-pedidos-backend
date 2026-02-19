package com.golden.erp.infrastructure.persistence.order;

import com.golden.erp.domain.order.entity.Order;
import com.golden.erp.domain.order.repository.OrderRepository;
import com.golden.erp.domain.order.valueobject.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository jpaRepository;
    private final OrderMapper mapper;

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = mapper.toJpaEntity(order);
        OrderJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<Order> findAll(OrderStatus status, Long clienteId, Pageable pageable) {
        return jpaRepository.findAllWithFilters(status, clienteId, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public List<Order> findByStatusAndDataCriacaoBefore(OrderStatus status, LocalDateTime dateTime) {
        return jpaRepository.findByStatusAndDataCriacaoBefore(status, dateTime)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
