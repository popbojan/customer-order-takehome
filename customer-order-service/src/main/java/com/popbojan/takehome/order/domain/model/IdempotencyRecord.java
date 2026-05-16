package com.popbojan.takehome.order.domain.model;

import java.util.UUID;

public record IdempotencyRecord(String key, String payloadHash, UUID orderId) {
}
