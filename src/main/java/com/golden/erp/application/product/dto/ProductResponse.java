package com.golden.erp.application.product.dto;

import com.golden.erp.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String sku;
    private String nome;
    private BigDecimal precoBruto;
    private Integer estoque;
    private Integer estoqueMinimo;
    private Boolean ativo;

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .nome(product.getNome())
                .precoBruto(product.getPrecoBruto())
                .estoque(product.getEstoque())
                .estoqueMinimo(product.getEstoqueMinimo())
                .ativo(product.getAtivo())
                .build();
    }
}
