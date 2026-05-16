package com.popbojan.takehome.order.domain.port;

public interface ProductCatalogPort {

    boolean existsById(String productOfferingId);
}
