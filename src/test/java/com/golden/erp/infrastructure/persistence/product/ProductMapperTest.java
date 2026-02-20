package com.golden.erp.infrastructure.persistence.product;

import com.golden.erp.domain.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    private final ProductMapper mapper = new ProductMapper();

    @Test
    @DisplayName("Deve converter JpaEntity para Domain")
    void shouldMapToDomain() {
        ProductJpaEntity entity = ProductJpaEntity.builder()
                .id(1L).sku("SKU-001").nome("Camiseta")
                .precoBruto(new BigDecimal("49.90"))
                .estoque(100).estoqueMinimo(10).ativo(true)
                .build();

        Product result = mapper.toDomain(entity);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSku()).isEqualTo("SKU-001");
        assertThat(result.getNome()).isEqualTo("Camiseta");
        assertThat(result.getPrecoBruto()).isEqualByComparingTo(new BigDecimal("49.90"));
        assertThat(result.getEstoque()).isEqualTo(100);
        assertThat(result.getEstoqueMinimo()).isEqualTo(10);
        assertThat(result.getAtivo()).isTrue();
    }

    @Test
    @DisplayName("Deve retornar null quando entity eh null")
    void shouldReturnNullWhenEntityNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("Deve converter Domain para JpaEntity")
    void shouldMapToJpaEntity() {
        Product product = Product.builder()
                .id(1L).sku("SKU-001").nome("Camiseta")
                .precoBruto(new BigDecimal("49.90"))
                .estoque(100).estoqueMinimo(10).ativo(true)
                .build();

        ProductJpaEntity result = mapper.toJpaEntity(product);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSku()).isEqualTo("SKU-001");
        assertThat(result.getNome()).isEqualTo("Camiseta");
    }

    @Test
    @DisplayName("Deve retornar null quando domain eh null")
    void shouldReturnNullWhenDomainNull() {
        assertThat(mapper.toJpaEntity(null)).isNull();
    }
}
