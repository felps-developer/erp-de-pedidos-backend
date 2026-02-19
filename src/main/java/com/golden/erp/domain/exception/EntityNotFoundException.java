package com.golden.erp.domain.exception;

public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String entity, Object id) {
        super("%s não encontrado(a) com id: %s".formatted(entity, id));
    }
}
