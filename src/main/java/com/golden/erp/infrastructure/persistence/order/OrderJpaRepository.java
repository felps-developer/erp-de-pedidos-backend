package com.golden.erp.infrastructure.persistence.order;

import com.golden.erp.domain.order.valueobject.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {

    @Query("""
            SELECT o FROM OrderJpaEntity o
            WHERE (:status IS NULL OR o.status = :status)
            AND (:clienteId IS NULL OR o.clienteId = :clienteId)
            """)
    Page<OrderJpaEntity> findAllWithFilters(
            @Param("status") OrderStatus status,
            @Param("clienteId") Long clienteId,
            Pageable pageable);

    List<OrderJpaEntity> findByStatusAndDataCriacaoBefore(OrderStatus status, LocalDateTime dateTime);
}
