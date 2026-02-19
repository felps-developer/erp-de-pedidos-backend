package com.golden.erp.application.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderUsdResponse {

    private Long orderId;
    private BigDecimal totalBrl;
    private BigDecimal totalUsd;
    private BigDecimal exchangeRate;
}
