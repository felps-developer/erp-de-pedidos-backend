package com.golden.erp.application.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateProductRequest {

    @NotBlank(message = "SKU é obrigatório")
    private String sku;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Preço bruto é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço bruto deve ser maior que zero")
    private BigDecimal precoBruto;

    @NotNull(message = "Estoque é obrigatório")
    @Min(value = 0, message = "Estoque não pode ser negativo")
    private Integer estoque;

    @NotNull(message = "Estoque mínimo é obrigatório")
    @Min(value = 0, message = "Estoque mínimo não pode ser negativo")
    private Integer estoqueMinimo;

    private Boolean ativo = true;
}
