package com.popbojan.takehome.order.adapters.driven.persistence;

import com.popbojan.takehome.order.domain.model.IdempotencyRecord;
import com.popbojan.takehome.order.domain.port.IdempotencyPort;
import java.sql.PreparedStatement;
import java.time.Instant;
import java.util.Optional;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JpaIdempotencyAdapter implements IdempotencyPort {

    private static final int IDEMPOTENCY_LOCK_CLASS_ID = 5_812_001;

    private final IdempotencyRepository repository;
    private final JdbcTemplate jdbcTemplate;

    public JpaIdempotencyAdapter(IdempotencyRepository repository, JdbcTemplate jdbcTemplate) {
        this.repository = repository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void lockKey(String key) {
        jdbcTemplate.execute((ConnectionCallback<Void>) conn -> {
            try (PreparedStatement statement = conn.prepareStatement(
                    "SELECT pg_advisory_xact_lock(?, hashtext(cast(? AS text)))")) {
                statement.setInt(1, IDEMPOTENCY_LOCK_CLASS_ID);
                statement.setString(2, key);
                statement.execute();
            }
            return null;
        });
    }

    @Override
    public Optional<IdempotencyRecord> findByKey(String lookupKey) {
        return repository.findById(lookupKey)
                .map(entity ->
                        new IdempotencyRecord(entity.getKey(), entity.getPayloadHash(), entity.getOrderId()));
    }

    @Override
    public void save(IdempotencyRecord record) {
        repository.save(new IdempotencyEntity(record.key(), record.payloadHash(), record.orderId(), Instant.now()));
    }
}
