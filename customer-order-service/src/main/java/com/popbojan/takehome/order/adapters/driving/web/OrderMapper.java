package com.popbojan.takehome.order.adapters.driving.web;

import com.popbojan.takehome.order.adapters.driving.web.request.CreateOrderRequest;
import com.popbojan.takehome.order.adapters.driving.web.request.PatchOrderRequest;
import com.popbojan.takehome.order.adapters.driving.web.response.OrderResponse;
import com.popbojan.takehome.order.domain.model.CreateOrderInput;
import com.popbojan.takehome.order.domain.model.CustomerOrder;
import com.popbojan.takehome.order.domain.model.OrderItem;
import com.popbojan.takehome.order.domain.model.OrderState;
import com.popbojan.takehome.order.domain.model.PatchOrderInput;
import com.popbojan.takehome.order.domain.model.PaymentMethod;
import java.util.Optional;
import java.util.UUID;

public class OrderMapper {

    public CreateOrderInput mapToInput(CreateOrderRequest request, String idempotencyKey, String payloadHash) {
        return new CreateOrderInput(
                request.category(),
                request.customer().id(),
                request.site().id(),
                request.orderItems().stream()
                        .map(item -> new OrderItem(item.productOfferingId(), item.quantity()))
                        .toList(),
                new PaymentMethod(request.paymentMethod().type(), request.paymentMethod().iban()),
                idempotencyKey,
                payloadHash
        );
    }

    public PatchOrderInput mapToInput(UUID id, PatchOrderRequest request) {
        return new PatchOrderInput(
                id,
                Optional.ofNullable(request.state()).map(OrderState::fromApiValue),
                Optional.ofNullable(request.category()),
                Optional.ofNullable(request.customer()).map(CreateOrderRequest.NestedId::id),
                Optional.ofNullable(request.site()).map(CreateOrderRequest.NestedId::id),
                Optional.ofNullable(request.orderItems()).map(items -> items.stream()
                        .map(item -> new OrderItem(item.productOfferingId(), item.quantity()))
                        .toList()),
                Optional.ofNullable(request.paymentMethod())
                        .map(payment -> new PaymentMethod(payment.type(), payment.iban()))
        );
    }

    public OrderResponse mapToResponse(CustomerOrder order) {
        return new OrderResponse(
                order.id(),
                order.state().apiValue(),
                order.category().name(),
                new OrderResponse.NestedId(order.customerId()),
                new OrderResponse.NestedId(order.siteId()),
                order.orderItems().stream()
                        .map(item -> new OrderResponse.OrderItemResponse(item.productOfferingId(), item.quantity()))
                        .toList(),
                new OrderResponse.PaymentMethodResponse(
                        order.paymentMethod().type().name(),
                        order.paymentMethod().iban()
                ),
                order.createdAt(),
                order.updatedAt()
        );
    }
}
