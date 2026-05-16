package com.popbojan.takehome.order.adapters.driven.persistence;

import com.popbojan.takehome.order.domain.model.OrderCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
class JpaCustomerOrderQueries {

    @PersistenceContext
    private EntityManager entityManager;

    List<CustomerOrderEntity> findPage(OrderCategory category, int limit, int offset) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CustomerOrderEntity> cq = cb.createQuery(CustomerOrderEntity.class);
        Root<CustomerOrderEntity> root = cq.from(CustomerOrderEntity.class);
        if (category != null) {
            cq.where(cb.equal(root.get("category"), category));
        }
        cq.orderBy(cb.desc(root.get("createdAt")));
        return entityManager.createQuery(cq).setFirstResult(offset).setMaxResults(limit).getResultList();
    }
}
