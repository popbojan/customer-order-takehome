package com.popbojan.takehome.order.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record PatchOrderInput(
        UUID id,
        Optional<OrderState> state,
        Optional<OrderCategory> category,
        Optional<String> customerId,
        Optional<String> siteId,
        Optional<List<OrderItem>> orderItems,
        Optional<PaymentMethod> paymentMethod
) {

    public boolean changesPayloadFields() {
        return category.isPresent()
                || customerId.isPresent()
                || siteId.isPresent()
                || orderItems.isPresent()
                || paymentMethod.isPresent();
    }
}
