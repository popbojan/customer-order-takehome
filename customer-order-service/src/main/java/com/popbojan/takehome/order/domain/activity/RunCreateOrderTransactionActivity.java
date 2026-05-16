package com.popbojan.takehome.order.domain.activity;

import com.popbojan.takehome.order.domain.model.CreateOrderInput;
import com.popbojan.takehome.order.domain.model.CreateOrderResult;
import com.popbojan.takehome.order.domain.port.OrderCreateTxnPort;

public class RunCreateOrderTransactionActivity {

    private final OrderCreateTxnPort orderCreateTxnPort;

    public RunCreateOrderTransactionActivity(OrderCreateTxnPort orderCreateTxnPort) {
        this.orderCreateTxnPort = orderCreateTxnPort;
    }

    public CreateOrderResult execute(CreateOrderInput input) {
        return orderCreateTxnPort.persistInTransaction(input);
    }
}
