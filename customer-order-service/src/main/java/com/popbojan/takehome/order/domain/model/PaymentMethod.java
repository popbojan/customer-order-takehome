package com.popbojan.takehome.order.domain.model;

public record PaymentMethod(PaymentMethodType type, String iban) {
}
