package com.popbojan.takehome.order.domain.port;

import com.popbojan.takehome.order.domain.model.CustomerOrder;
import com.popbojan.takehome.order.domain.model.OrderCategory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerOrderPort {

    CustomerOrder save(CustomerOrder order);

    Optional<CustomerOrder> findById(UUID id);

    List<CustomerOrder> find(OrderCategory category, int limit, int offset);

    long count(OrderCategory category);
}
