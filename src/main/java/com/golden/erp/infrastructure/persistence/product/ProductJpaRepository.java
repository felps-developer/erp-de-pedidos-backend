package com.golden.erp.infrastructure.persistence.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);

    @Query("""
            SELECT p FROM ProductJpaEntity p
            WHERE (CAST(:ativo AS boolean) IS NULL OR p.ativo = :ativo)
            """)
    Page<ProductJpaEntity> findAllWithFilters(@Param("ativo") Boolean ativo, Pageable pageable);

    @Query("SELECT p FROM ProductJpaEntity p WHERE p.estoque < p.estoqueMinimo")
    List<ProductJpaEntity> findAllWithLowStock();
}
