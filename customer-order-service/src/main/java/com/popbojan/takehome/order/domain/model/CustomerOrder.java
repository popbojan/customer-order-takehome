package com.popbojan.takehome.order.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CustomerOrder(
        UUID id,
        OrderState state,
        OrderCategory category,
        String customerId,
        String siteId,
        List<OrderItem> orderItems,
        PaymentMethod paymentMethod,
        Instant createdAt,
        Instant updatedAt
) {
}
