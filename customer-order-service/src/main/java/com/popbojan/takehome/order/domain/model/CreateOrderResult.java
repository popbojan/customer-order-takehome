package com.popbojan.takehome.order.domain.model;

public record CreateOrderResult(CustomerOrder order, boolean replayed) {
}
