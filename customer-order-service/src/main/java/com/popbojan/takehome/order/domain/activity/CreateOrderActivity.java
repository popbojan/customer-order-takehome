package com.popbojan.takehome.order.domain.activity;

import com.popbojan.takehome.order.domain.model.CreateOrderInput;
import com.popbojan.takehome.order.domain.model.CustomerOrder;
import com.popbojan.takehome.order.domain.model.OrderState;
import com.popbojan.takehome.order.domain.port.CustomerOrderPort;
import java.time.Instant;
import java.util.UUID;

public class CreateOrderActivity {

    private final CustomerOrderPort customerOrderPort;

    public CreateOrderActivity(CustomerOrderPort customerOrderPort) {
        this.customerOrderPort = customerOrderPort;
    }

    public CustomerOrder execute(CreateOrderInput input) {
        Instant now = Instant.now();
        CustomerOrder order = new CustomerOrder(
                UUID.randomUUID(),
                OrderState.DRAFT,
                input.category(),
                input.customerId(),
                input.siteId(),
                input.orderItems(),
                input.paymentMethod(),
                now,
                now
        );
        return customerOrderPort.save(order);
    }
}
