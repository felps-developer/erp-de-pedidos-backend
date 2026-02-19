package com.golden.erp.domain.order.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private Long id;
    private Long produtoId;
    private String produtoNome;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal desconto;

    public BigDecimal getSubtotal() {
        BigDecimal total = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getDescontoTotal() {
        if (desconto == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return desconto.multiply(BigDecimal.valueOf(quantidade)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotal() {
        return getSubtotal().subtract(getDescontoTotal()).setScale(2, RoundingMode.HALF_UP);
    }
}
