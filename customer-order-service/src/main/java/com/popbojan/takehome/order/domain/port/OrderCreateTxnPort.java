package com.popbojan.takehome.order.domain.port;

import com.popbojan.takehome.order.domain.model.CreateOrderInput;
import com.popbojan.takehome.order.domain.model.CreateOrderResult;

/** Driven persistence port: one atomic DB transaction per create (advisory lock, reads, inserts, idempotency row). */
public interface OrderCreateTxnPort {

    CreateOrderResult persistInTransaction(CreateOrderInput input);
}
