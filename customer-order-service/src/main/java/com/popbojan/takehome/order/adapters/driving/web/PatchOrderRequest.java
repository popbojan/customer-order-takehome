package com.popbojan.takehome.order.adapters.driving.web;

import com.popbojan.takehome.order.domain.model.OrderCategory;
import java.util.List;

public record PatchOrderRequest(
        String state,
        OrderCategory category,
        CreateOrderRequest.NestedId customer,
        CreateOrderRequest.NestedId site,
        List<OrderItemRequest> orderItems,
        PaymentMethodRequest paymentMethod
) {
}
