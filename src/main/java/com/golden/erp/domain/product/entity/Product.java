package com.golden.erp.domain.product.entity;

import com.golden.erp.domain.exception.InsufficientStockException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private Long id;
    private String sku;
    private String nome;
    private BigDecimal precoBruto;
    private Integer estoque;
    private Integer estoqueMinimo;
    private Boolean ativo;

    public void decreaseStock(int quantity) {
        if (this.estoque < quantity) {
            throw new InsufficientStockException(this.nome, this.estoque, quantity);
        }
        this.estoque -= quantity;
    }

    public void increaseStock(int quantity) {
        this.estoque += quantity;
    }

    public boolean isLowStock() {
        return this.estoque < this.estoqueMinimo;
    }
}
