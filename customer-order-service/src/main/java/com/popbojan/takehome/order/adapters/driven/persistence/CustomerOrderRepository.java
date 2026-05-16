package com.popbojan.takehome.order.adapters.driven.persistence;

import com.popbojan.takehome.order.domain.model.OrderCategory;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrderEntity, UUID> {

    long countByCategory(OrderCategory category);
}
