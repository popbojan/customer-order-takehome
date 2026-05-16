package com.popbojan.takehome.order.adapters.driving.web.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String state,
        String category,
        NestedId customer,
        NestedId site,
        List<OrderItemResponse> orderItems,
        PaymentMethodResponse paymentMethod,
        Instant createdAt,
        Instant updatedAt
) {
    public record NestedId(String id) {
    }

    public record OrderItemResponse(String productOfferingId, int quantity) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PaymentMethodResponse(String type, String iban) {
    }
}
