package com.popbojan.takehome.order.adapters.driving.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record OrderItemRequest(
        @NotBlank String productOfferingId,
        @Min(1) int quantity
) {
}
