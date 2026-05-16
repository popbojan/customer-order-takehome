package com.popbojan.takehome.order.domain;

import com.popbojan.takehome.order.domain.activity.RunCreateOrderTransactionActivity;
import com.popbojan.takehome.order.domain.model.CreateOrderInput;
import com.popbojan.takehome.order.domain.model.CreateOrderResult;
import com.popbojan.takehome.order.domain.port.OrderCreateTxnPort;

/** Entry facade for REST; delegates to {@link RunCreateOrderTransactionActivity} → {@link OrderCreateTxnPort}. */
public class CreateOrderUseCase {

    private final RunCreateOrderTransactionActivity runCreateOrderTransactionActivity;

    public CreateOrderUseCase(RunCreateOrderTransactionActivity runCreateOrderTransactionActivity) {
        this.runCreateOrderTransactionActivity = runCreateOrderTransactionActivity;
    }

    public CreateOrderResult execute(CreateOrderInput input) {
        return runCreateOrderTransactionActivity.execute(input);
    }
}
