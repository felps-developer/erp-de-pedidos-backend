package com.golden.erp.application.product.service;

import com.golden.erp.application.product.dto.CreateProductRequest;
import com.golden.erp.application.product.dto.ProductResponse;
import com.golden.erp.application.product.dto.UpdateProductRequest;
import com.golden.erp.domain.exception.DuplicateFieldException;
import com.golden.erp.domain.exception.EntityNotFoundException;
import com.golden.erp.domain.product.entity.Product;
import com.golden.erp.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = Product.builder()
                .id(1L)
                .sku("SKU-001")
                .nome("Camiseta")
                .precoBruto(new BigDecimal("49.90"))
                .estoque(100)
                .estoqueMinimo(10)
                .ativo(true)
                .build();
    }

    @Nested
    @DisplayName("Criar Produto")
    class CreateProduct {

        @Test
        @DisplayName("Deve criar produto com sucesso")
        void shouldCreateProduct() {
            CreateProductRequest request = CreateProductRequest.builder()
                    .sku("SKU-001")
                    .nome("Camiseta")
                    .precoBruto(new BigDecimal("49.90"))
                    .estoque(100)
                    .estoqueMinimo(10)
                    .ativo(true)
                    .build();

            when(productRepository.existsBySku("SKU-001")).thenReturn(false);
            when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

            ProductResponse response = productService.create(request);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getSku()).isEqualTo("SKU-001");
            assertThat(response.getNome()).isEqualTo("Camiseta");
            assertThat(response.getPrecoBruto()).isEqualByComparingTo(new BigDecimal("49.90"));
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando SKU já existe")
        void shouldThrowWhenSkuExists() {
            CreateProductRequest request = CreateProductRequest.builder()
                    .sku("SKU-001")
                    .nome("Camiseta")
                    .precoBruto(new BigDecimal("49.90"))
                    .estoque(100)
                    .estoqueMinimo(10)
                    .build();

            when(productRepository.existsBySku("SKU-001")).thenReturn(true);

            assertThatThrownBy(() -> productService.create(request))
                    .isInstanceOf(DuplicateFieldException.class)
                    .hasMessageContaining("sku");
        }

        @Test
        @DisplayName("Deve definir ativo como true por padrão")
        void shouldDefaultActiveToTrue() {
            CreateProductRequest request = CreateProductRequest.builder()
                    .sku("SKU-002")
                    .nome("Calça")
                    .precoBruto(new BigDecimal("99.90"))
                    .estoque(50)
                    .estoqueMinimo(5)
                    .ativo(null)
                    .build();

            when(productRepository.existsBySku(anyString())).thenReturn(false);
            when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

            productService.create(request);

            verify(productRepository).save(any(Product.class));
        }
    }

    @Nested
    @DisplayName("Buscar Produto")
    class FindProduct {

        @Test
        @DisplayName("Deve retornar produto por ID")
        void shouldFindById() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

            ProductResponse response = productService.findById(1L);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getSku()).isEqualTo("SKU-001");
        }

        @Test
        @DisplayName("Deve lançar exceção quando produto não encontrado")
        void shouldThrowWhenNotFound() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.findById(99L))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("Deve retornar produtos paginados filtrados por ativo")
        void shouldFindAllFilteredByActive() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> page = new PageImpl<>(List.of(sampleProduct));
            when(productRepository.findAll(true, pageable)).thenReturn(page);

            Page<ProductResponse> result = productService.findAll(true, pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getAtivo()).isTrue();
        }
    }

    @Nested
    @DisplayName("Atualizar Produto")
    class UpdateProduct {

        @Test
        @DisplayName("Deve atualizar produto com sucesso")
        void shouldUpdateProduct() {
            UpdateProductRequest request = UpdateProductRequest.builder()
                    .sku("SKU-001")
                    .nome("Camiseta Atualizada")
                    .precoBruto(new BigDecimal("59.90"))
                    .estoque(80)
                    .estoqueMinimo(10)
                    .ativo(true)
                    .build();

            Product updatedProduct = Product.builder()
                    .id(1L)
                    .sku("SKU-001")
                    .nome("Camiseta Atualizada")
                    .precoBruto(new BigDecimal("59.90"))
                    .estoque(80)
                    .estoqueMinimo(10)
                    .ativo(true)
                    .build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
            when(productRepository.existsBySkuAndIdNot("SKU-001", 1L)).thenReturn(false);
            when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

            ProductResponse response = productService.update(1L, request);

            assertThat(response.getNome()).isEqualTo("Camiseta Atualizada");
            assertThat(response.getPrecoBruto()).isEqualByComparingTo(new BigDecimal("59.90"));
        }

        @Test
        @DisplayName("Deve lançar exceção quando SKU duplicado na atualização")
        void shouldThrowWhenSkuDuplicateOnUpdate() {
            UpdateProductRequest request = UpdateProductRequest.builder()
                    .sku("SKU-DUPLICATE")
                    .nome("Test")
                    .precoBruto(new BigDecimal("10.00"))
                    .estoque(10)
                    .estoqueMinimo(1)
                    .ativo(true)
                    .build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
            when(productRepository.existsBySkuAndIdNot("SKU-DUPLICATE", 1L)).thenReturn(true);

            assertThatThrownBy(() -> productService.update(1L, request))
                    .isInstanceOf(DuplicateFieldException.class);
        }
    }

    @Nested
    @DisplayName("Deletar Produto")
    class DeleteProduct {

        @Test
        @DisplayName("Deve deletar produto com sucesso")
        void shouldDeleteProduct() {
            when(productRepository.existsById(1L)).thenReturn(true);

            productService.delete(1L);

            verify(productRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Deve lançar exceção ao deletar produto inexistente")
        void shouldThrowWhenDeletingNonExistent() {
            when(productRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> productService.delete(99L))
                    .isInstanceOf(EntityNotFoundException.class);

            verify(productRepository, never()).deleteById(any());
        }
    }
}
