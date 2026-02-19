package com.golden.erp.infrastructure.persistence.product;

import com.golden.erp.domain.product.entity.Product;
import com.golden.erp.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final ProductMapper mapper;

    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = mapper.toJpaEntity(product);
        ProductJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<Product> findAll(Boolean ativo, Pageable pageable) {
        return jpaRepository.findAllWithFilters(ativo, pageable).map(mapper::toDomain);
    }

    @Override
    public boolean existsBySku(String sku) {
        return jpaRepository.existsBySku(sku);
    }

    @Override
    public boolean existsBySkuAndIdNot(String sku, Long id) {
        return jpaRepository.existsBySkuAndIdNot(sku, id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<Product> findAllWithLowStock() {
        return jpaRepository.findAllWithLowStock().stream()
                .map(mapper::toDomain)
                .toList();
    }
}
