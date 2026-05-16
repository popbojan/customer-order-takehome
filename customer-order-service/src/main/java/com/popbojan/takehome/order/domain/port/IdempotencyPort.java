package com.popbojan.takehome.order.domain.port;

import com.popbojan.takehome.order.domain.model.IdempotencyRecord;
import java.util.Optional;

public interface IdempotencyPort {

    /**
     * Serializes concurrent creates that share an idempotency key (PostgreSQL transaction-scoped advisory lock).
     */
    void lockKey(String key);

    Optional<IdempotencyRecord> findByKey(String key);

    void save(IdempotencyRecord record);
}
