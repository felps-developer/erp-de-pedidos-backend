package com.golden.erp.infrastructure.persistence.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_sku", columnList = "sku", unique = true),
        @Index(name = "idx_product_ativo", columnList = "ativo")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String nome;

    @Column(name = "preco_bruto", nullable = false, precision = 12, scale = 2)
    private BigDecimal precoBruto;

    @Column(nullable = false)
    private Integer estoque;

    @Column(name = "estoque_minimo", nullable = false)
    private Integer estoqueMinimo;

    @Column(nullable = false)
    private Boolean ativo;
}
