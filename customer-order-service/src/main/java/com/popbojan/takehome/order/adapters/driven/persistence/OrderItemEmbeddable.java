package com.popbojan.takehome.order.adapters.driven.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class OrderItemEmbeddable {

    @Column(name = "product_offering_id", nullable = false)
    private String productOfferingId;

    @Column(nullable = false)
    private int quantity;

    protected OrderItemEmbeddable() {
    }

    OrderItemEmbeddable(String productOfferingId, int quantity) {
        this.productOfferingId = productOfferingId;
        this.quantity = quantity;
    }

    public String getProductOfferingId() {
        return productOfferingId;
    }

    public int getQuantity() {
        return quantity;
    }
}
