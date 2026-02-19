package com.golden.erp.domain.exception;

public class InvalidOrderStateException extends DomainException {

    public InvalidOrderStateException(String currentStatus, String targetStatus) {
        super("Não é possível alterar o status do pedido de %s para %s"
                .formatted(currentStatus, targetStatus));
    }
}
