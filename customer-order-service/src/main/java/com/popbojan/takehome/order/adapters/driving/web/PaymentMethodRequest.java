package com.popbojan.takehome.order.adapters.driving.web;

import com.popbojan.takehome.order.domain.model.PaymentMethodType;
import jakarta.validation.constraints.NotNull;

public record PaymentMethodRequest(
        @NotNull PaymentMethodType type,
        String iban
) {
}
