package com.popbojan.takehome.order.domain.exception;

public class UnknownProductOfferingException extends DomainException {

    public UnknownProductOfferingException(String productOfferingId) {
        super("Unknown product offering: " + productOfferingId);
    }
}
