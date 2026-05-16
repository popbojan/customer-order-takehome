package com.popbojan.takehome.order.adapters.driven.persistence;

import com.popbojan.takehome.order.domain.model.CustomerOrder;
import com.popbojan.takehome.order.domain.model.OrderCategory;
import com.popbojan.takehome.order.domain.model.OrderItem;
import com.popbojan.takehome.order.domain.model.PaymentMethod;
import com.popbojan.takehome.order.domain.port.CustomerOrderPort;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JpaCustomerOrderAdapter implements CustomerOrderPort {

    private final CustomerOrderRepository repository;

    public JpaCustomerOrderAdapter(CustomerOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public CustomerOrder save(CustomerOrder order) {
        return mapToDomain(repository.save(mapToEntity(order)));
    }

    @Override
    public Optional<CustomerOrder> findById(UUID id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    @Override
    public List<CustomerOrder> find(OrderCategory category, int limit, int offset) {
        var page = PageRequest.of(offset / limit, limit);
        if (category != null) {
            return repository.findByCategoryOrderByCreatedAtDesc(category, page).stream().map(this::mapToDomain).toList();
        }
        return repository.findAllByOrderByCreatedAtDesc(page).stream().map(this::mapToDomain).toList();
    }

    @Override
    public long count(OrderCategory category) {
        return category == null ? repository.count() : repository.countByCategory(category);
    }

    private CustomerOrderEntity mapToEntity(CustomerOrder order) {
        return new CustomerOrderEntity(
                order.id(),
                order.state(),
                order.category(),
                order.customerId(),
                order.siteId(),
                order.orderItems().stream()
                        .map(item -> new OrderItemEmbeddable(item.productOfferingId(), item.quantity()))
                        .toList(),
                order.paymentMethod().type(),
                order.paymentMethod().iban(),
                order.createdAt(),
                order.updatedAt()
        );
    }

    private CustomerOrder mapToDomain(CustomerOrderEntity entity) {
        return new CustomerOrder(
                entity.getId(),
                entity.getState(),
                entity.getCategory(),
                entity.getCustomerId(),
                entity.getSiteId(),
                entity.getOrderItems().stream()
                        .map(item -> new OrderItem(item.getProductOfferingId(), item.getQuantity()))
                        .toList(),
                new PaymentMethod(entity.getPaymentType(), entity.getPaymentIban()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
