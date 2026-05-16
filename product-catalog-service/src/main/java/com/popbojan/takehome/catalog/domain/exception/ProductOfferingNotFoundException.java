package com.popbojan.takehome.catalog.domain.exception;

public class ProductOfferingNotFoundException extends RuntimeException {

    private final String productOfferingId;

    public ProductOfferingNotFoundException(String productOfferingId) {
        super("Product offering not found: " + productOfferingId);
        this.productOfferingId = productOfferingId;
    }

    public String productOfferingId() {
        return productOfferingId;
    }
}
