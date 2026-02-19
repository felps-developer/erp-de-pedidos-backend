package com.golden.erp.domain.exception;

public class InsufficientStockException extends DomainException {

    public InsufficientStockException(String productName, int available, int requested) {
        super("Estoque insuficiente para '%s'. Disponível: %d, Solicitado: %d"
                .formatted(productName, available, requested));
    }
}
