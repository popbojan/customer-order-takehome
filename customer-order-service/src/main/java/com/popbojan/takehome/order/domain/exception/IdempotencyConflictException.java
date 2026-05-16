package com.popbojan.takehome.order.domain.exception;

public class IdempotencyConflictException extends DomainException {

    public IdempotencyConflictException() {
        super("Idempotency-Key was already used with a different payload");
    }
}
