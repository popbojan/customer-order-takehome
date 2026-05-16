package com.popbojan.takehome.order.adapters.driving.web.request;

import com.popbojan.takehome.order.domain.model.OrderCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateOrderRequest(
        @NotNull OrderCategory category,
        @Valid @NotNull NestedId customer,
        @Valid @NotNull NestedId site,
        @Valid @NotEmpty List<OrderItemRequest> orderItems,
        @Valid @NotNull PaymentMethodRequest paymentMethod
) {
    public record NestedId(@NotBlank String id) {
    }
}
