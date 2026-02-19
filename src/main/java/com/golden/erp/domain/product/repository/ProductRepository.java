package com.golden.erp.domain.product.repository;

import com.golden.erp.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    Page<Product> findAll(Boolean ativo, Pageable pageable);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);

    void deleteById(Long id);

    boolean existsById(Long id);

    List<Product> findAllWithLowStock();
}
