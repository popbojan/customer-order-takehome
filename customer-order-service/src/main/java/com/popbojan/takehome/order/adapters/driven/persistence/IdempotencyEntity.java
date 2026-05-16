package com.popbojan.takehome.order.adapters.driven.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "idempotency_records")
public class IdempotencyEntity {

    @Id
    @Column(name = "idempotency_key")
    private String key;

    @Column(name = "payload_hash", nullable = false)
    private String payloadHash;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected IdempotencyEntity() {
    }

    IdempotencyEntity(String key, String payloadHash, UUID orderId, Instant createdAt) {
        this.key = key;
        this.payloadHash = payloadHash;
        this.orderId = orderId;
        this.createdAt = createdAt;
    }

    public String getKey() {
        return key;
    }

    public String getPayloadHash() {
        return payloadHash;
    }

    public UUID getOrderId() {
        return orderId;
    }
}
