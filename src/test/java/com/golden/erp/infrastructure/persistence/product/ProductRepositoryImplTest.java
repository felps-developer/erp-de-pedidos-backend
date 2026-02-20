package com.golden.erp.infrastructure.persistence.product;

import com.golden.erp.domain.product.entity.Product;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryImplTest {

    @Mock
    private ProductJpaRepository jpaRepository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductRepositoryImpl repository;

    private Product sampleProduct;
    private ProductJpaEntity sampleEntity;

    @BeforeEach
    void setUp() {
        sampleProduct = Product.builder()
                .id(1L).sku("SKU-001").nome("Camiseta")
                .precoBruto(new BigDecimal("49.90")).estoque(100).estoqueMinimo(10).ativo(true)
                .build();

        sampleEntity = ProductJpaEntity.builder()
                .id(1L).sku("SKU-001").nome("Camiseta")
                .precoBruto(new BigDecimal("49.90")).estoque(100).estoqueMinimo(10).ativo(true)
                .build();
    }

    @Test
    @DisplayName("Deve salvar produto")
    void shouldSave() {
        when(mapper.toJpaEntity(sampleProduct)).thenReturn(sampleEntity);
        when(jpaRepository.save(sampleEntity)).thenReturn(sampleEntity);
        when(mapper.toDomain(sampleEntity)).thenReturn(sampleProduct);

        Product result = repository.save(sampleProduct);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve buscar por ID")
    void shouldFindById() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(mapper.toDomain(sampleEntity)).thenReturn(sampleProduct);

        Optional<Product> result = repository.findById(1L);

        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("Deve buscar todos com filtro de ativo")
    void shouldFindAllWithFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductJpaEntity> page = new PageImpl<>(List.of(sampleEntity));
        when(jpaRepository.findAllWithFilters(true, pageable)).thenReturn(page);
        when(mapper.toDomain(sampleEntity)).thenReturn(sampleProduct);

        Page<Product> result = repository.findAll(true, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve verificar existencia por SKU")
    void shouldCheckExistsBySku() {
        when(jpaRepository.existsBySku("SKU-001")).thenReturn(true);
        assertThat(repository.existsBySku("SKU-001")).isTrue();
    }

    @Test
    @DisplayName("Deve verificar existencia por SKU excluindo ID")
    void shouldCheckExistsBySkuAndIdNot() {
        when(jpaRepository.existsBySkuAndIdNot("SKU-001", 1L)).thenReturn(false);
        assertThat(repository.existsBySkuAndIdNot("SKU-001", 1L)).isFalse();
    }

    @Test
    @DisplayName("Deve deletar por ID")
    void shouldDeleteById() {
        repository.deleteById(1L);
        verify(jpaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve verificar existencia por ID")
    void shouldCheckExistsById() {
        when(jpaRepository.existsById(1L)).thenReturn(true);
        assertThat(repository.existsById(1L)).isTrue();
    }

    @Test
    @DisplayName("Deve buscar produtos com estoque baixo")
    void shouldFindAllWithLowStock() {
        when(jpaRepository.findAllWithLowStock()).thenReturn(List.of(sampleEntity));
        when(mapper.toDomain(sampleEntity)).thenReturn(sampleProduct);

        List<Product> result = repository.findAllWithLowStock();

        assertThat(result).hasSize(1);
    }
}
