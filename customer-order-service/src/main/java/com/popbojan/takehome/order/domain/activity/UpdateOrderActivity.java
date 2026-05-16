package com.popbojan.takehome.order.domain.activity;

import com.popbojan.takehome.order.domain.model.CustomerOrder;
import com.popbojan.takehome.order.domain.port.CustomerOrderPort;

public class UpdateOrderActivity {

    private final CustomerOrderPort customerOrderPort;

    public UpdateOrderActivity(CustomerOrderPort customerOrderPort) {
        this.customerOrderPort = customerOrderPort;
    }

    public CustomerOrder execute(CustomerOrder order) {
        return customerOrderPort.save(order);
    }
}
