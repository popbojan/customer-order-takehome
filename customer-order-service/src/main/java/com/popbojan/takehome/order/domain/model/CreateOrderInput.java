package com.popbojan.takehome.order.domain.model;

import java.util.List;

public record CreateOrderInput(
        OrderCategory category,
        String customerId,
        String siteId,
        List<OrderItem> orderItems,
        PaymentMethod paymentMethod,
        String idempotencyKey,
        String payloadHash
) {
}
