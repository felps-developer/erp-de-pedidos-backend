package com.golden.erp.infrastructure.persistence.product;

import com.golden.erp.domain.product.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toDomain(ProductJpaEntity entity) {
        if (entity == null) return null;

        return Product.builder()
                .id(entity.getId())
                .sku(entity.getSku())
                .nome(entity.getNome())
                .precoBruto(entity.getPrecoBruto())
                .estoque(entity.getEstoque())
                .estoqueMinimo(entity.getEstoqueMinimo())
                .ativo(entity.getAtivo())
                .build();
    }

    public ProductJpaEntity toJpaEntity(Product domain) {
        if (domain == null) return null;

        return ProductJpaEntity.builder()
                .id(domain.getId())
                .sku(domain.getSku())
                .nome(domain.getNome())
                .precoBruto(domain.getPrecoBruto())
                .estoque(domain.getEstoque())
                .estoqueMinimo(domain.getEstoqueMinimo())
                .ativo(domain.getAtivo())
                .build();
    }
}
