package com.popbojan.takehome.order.adapters.driven.persistence;

import com.popbojan.takehome.order.domain.model.OrderCategory;
import com.popbojan.takehome.order.domain.model.OrderState;
import com.popbojan.takehome.order.domain.model.PaymentMethodType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customer_orders")
public class CustomerOrderEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderState state;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderCategory category;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "site_id", nullable = false)
    private String siteId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "customer_order_items", joinColumns = @JoinColumn(name = "order_id"))
    @OrderColumn(name = "position")
    private List<OrderItemEmbeddable> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentMethodType paymentType;

    @Column(name = "payment_iban")
    private String paymentIban;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected CustomerOrderEntity() {
    }

    CustomerOrderEntity(
            UUID id,
            OrderState state,
            OrderCategory category,
            String customerId,
            String siteId,
            List<OrderItemEmbeddable> orderItems,
            PaymentMethodType paymentType,
            String paymentIban,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.state = state;
        this.category = category;
        this.customerId = customerId;
        this.siteId = siteId;
        this.orderItems = orderItems;
        this.paymentType = paymentType;
        this.paymentIban = paymentIban;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public OrderState getState() {
        return state;
    }

    public OrderCategory getCategory() {
        return category;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getSiteId() {
        return siteId;
    }

    public List<OrderItemEmbeddable> getOrderItems() {
        return orderItems;
    }

    public PaymentMethodType getPaymentType() {
        return paymentType;
    }

    public String getPaymentIban() {
        return paymentIban;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
