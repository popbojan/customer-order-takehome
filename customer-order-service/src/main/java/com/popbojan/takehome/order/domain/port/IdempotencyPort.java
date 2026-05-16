package com.popbojan.takehome.order.domain.port;

import com.popbojan.takehome.order.domain.model.IdempotencyRecord;
import java.util.Optional;

public interface IdempotencyPort {

    Optional<IdempotencyRecord> findByKey(String key);

    void save(IdempotencyRecord record);
}
