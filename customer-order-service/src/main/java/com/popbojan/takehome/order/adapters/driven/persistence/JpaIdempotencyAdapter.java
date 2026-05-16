package com.popbojan.takehome.order.adapters.driven.persistence;

import com.popbojan.takehome.order.domain.model.IdempotencyRecord;
import com.popbojan.takehome.order.domain.port.IdempotencyPort;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class JpaIdempotencyAdapter implements IdempotencyPort {

    private final IdempotencyRepository repository;

    public JpaIdempotencyAdapter(IdempotencyRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<IdempotencyRecord> findByKey(String key) {
        return repository.findById(key)
                .map(entity -> new IdempotencyRecord(entity.getKey(), entity.getPayloadHash(), entity.getOrderId()));
    }

    @Override
    public void save(IdempotencyRecord record) {
        repository.save(new IdempotencyEntity(record.key(), record.payloadHash(), record.orderId(), Instant.now()));
    }
}
