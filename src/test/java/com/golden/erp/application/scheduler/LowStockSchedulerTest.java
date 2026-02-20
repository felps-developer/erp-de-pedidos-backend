package com.golden.erp.application.scheduler;

import com.golden.erp.domain.product.entity.Product;
import com.golden.erp.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LowStockSchedulerTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private LowStockScheduler scheduler;

    @Test
    @DisplayName("Deve registrar produtos com estoque baixo")
    void shouldLogLowStockProducts() {
        Product lowStock = Product.builder()
                .id(1L).sku("SKU-001").nome("Camiseta")
                .precoBruto(new BigDecimal("49.90"))
                .estoque(2).estoqueMinimo(10).ativo(true)
                .build();

        when(productRepository.findAllWithLowStock()).thenReturn(List.of(lowStock));

        scheduler.checkLowStock();

        verify(productRepository).findAllWithLowStock();
    }

    @Test
    @DisplayName("Deve apenas logar quando todos os produtos estão com estoque adequado")
    void shouldLogWhenAllStockIsAdequate() {
        when(productRepository.findAllWithLowStock()).thenReturn(Collections.emptyList());

        scheduler.checkLowStock();

        verify(productRepository).findAllWithLowStock();
    }
}
