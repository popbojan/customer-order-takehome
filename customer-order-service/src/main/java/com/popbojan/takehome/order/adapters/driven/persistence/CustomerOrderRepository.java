package com.popbojan.takehome.order.adapters.driven.persistence;

import com.popbojan.takehome.order.domain.model.OrderCategory;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrderEntity, UUID> {

    java.util.List<CustomerOrderEntity> findByCategoryOrderByCreatedAtDesc(OrderCategory category, Pageable pageable);

    java.util.List<CustomerOrderEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByCategory(OrderCategory category);
}
