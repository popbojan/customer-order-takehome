package com.popbojan.takehome.order.domain;

import com.popbojan.takehome.order.domain.activity.ListOrdersActivity;
import com.popbojan.takehome.order.domain.model.CustomerOrder;
import com.popbojan.takehome.order.domain.model.OrderCategory;
import com.popbojan.takehome.order.domain.model.PageResult;

public class ListOrdersUseCase {

    private final ListOrdersActivity listOrdersActivity;

    public ListOrdersUseCase(ListOrdersActivity listOrdersActivity) {
        this.listOrdersActivity = listOrdersActivity;
    }

    public PageResult<CustomerOrder> execute(OrderCategory category, int limit, int offset) {
        int boundedLimit = Math.min(Math.max(limit, 1), 100);
        int boundedOffset = Math.max(offset, 0);
        return listOrdersActivity.execute(category, boundedLimit, boundedOffset);
    }
}
