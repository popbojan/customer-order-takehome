package com.popbojan.takehome.order.adapters.driving.web;

import com.popbojan.takehome.order.domain.model.OrderCategory;
import jakarta.validation.Valid;
import java.util.List;

public record PatchOrderRequest(
        String state,
        OrderCategory category,
        @Valid CreateOrderRequest.NestedId customer,
        @Valid CreateOrderRequest.NestedId site,
        List<@Valid OrderItemRequest> orderItems,
        @Valid PaymentMethodRequest paymentMethod
) {
}
