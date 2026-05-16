package com.popbojan.takehome.order.domain.model;

public enum OrderState {
    DRAFT,
    PREVIEW,
    SUBMITTED,
    CONFIRMED;

    public String apiValue() {
        return name().toLowerCase();
    }

    public static OrderState fromApiValue(String value) {
        return OrderState.valueOf(value.toUpperCase());
    }
}
