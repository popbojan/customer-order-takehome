package com.popbojan.takehome.order.domain.activity;

import com.popbojan.takehome.order.domain.model.CustomerOrder;
import com.popbojan.takehome.order.domain.model.OrderCategory;
import com.popbojan.takehome.order.domain.model.PageResult;
import com.popbojan.takehome.order.domain.port.CustomerOrderPort;

public class ListOrdersActivity {

    private final CustomerOrderPort customerOrderPort;

    public ListOrdersActivity(CustomerOrderPort customerOrderPort) {
        this.customerOrderPort = customerOrderPort;
    }

    public PageResult<CustomerOrder> execute(OrderCategory category, int limit, int offset) {
        return new PageResult<>(
                customerOrderPort.find(category, limit, offset),
                customerOrderPort.count(category),
                limit,
                offset
        );
    }
}
