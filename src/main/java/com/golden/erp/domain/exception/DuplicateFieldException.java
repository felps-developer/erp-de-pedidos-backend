package com.golden.erp.domain.exception;

public class DuplicateFieldException extends DomainException {

    public DuplicateFieldException(String field, String value) {
        super("Já existe um registro com %s: %s".formatted(field, value));
    }
}
