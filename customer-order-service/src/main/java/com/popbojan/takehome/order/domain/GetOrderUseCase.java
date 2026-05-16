package com.popbojan.takehome.order.domain;

import com.popbojan.takehome.order.domain.activity.GetOrderActivity;
import com.popbojan.takehome.order.domain.model.CustomerOrder;
import java.util.Optional;
import java.util.UUID;

public class GetOrderUseCase {

    private final GetOrderActivity getOrderActivity;

    public GetOrderUseCase(GetOrderActivity getOrderActivity) {
        this.getOrderActivity = getOrderActivity;
    }

    public Optional<CustomerOrder> execute(UUID id) {
        return getOrderActivity.execute(id);
    }
}
