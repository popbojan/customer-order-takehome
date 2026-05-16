package com.popbojan.takehome.order.domain.activity;

import com.popbojan.takehome.order.domain.model.CustomerOrder;
import com.popbojan.takehome.order.domain.port.CustomerOrderPort;
import java.util.Optional;
import java.util.UUID;

public class GetOrderActivity {

    private final CustomerOrderPort customerOrderPort;

    public GetOrderActivity(CustomerOrderPort customerOrderPort) {
        this.customerOrderPort = customerOrderPort;
    }

    public Optional<CustomerOrder> execute(UUID id) {
        return customerOrderPort.findById(id);
    }
}
