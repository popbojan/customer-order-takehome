package com.popbojan.takehome.order.domain;

import com.popbojan.takehome.order.domain.activity.CreateOrderActivity;
import com.popbojan.takehome.order.domain.activity.GetOrderActivity;
import com.popbojan.takehome.order.domain.activity.ValidateOrderActivity;
import com.popbojan.takehome.order.domain.activity.ValidateProductOfferingsActivity;
import com.popbojan.takehome.order.domain.exception.IdempotencyConflictException;
import com.popbojan.takehome.order.domain.exception.OrderNotFoundException;
import com.popbojan.takehome.order.domain.model.CreateOrderInput;
import com.popbojan.takehome.order.domain.model.CreateOrderResult;
import com.popbojan.takehome.order.domain.model.CustomerOrder;
import com.popbojan.takehome.order.domain.model.IdempotencyRecord;
import com.popbojan.takehome.order.domain.port.IdempotencyPort;

public class CreateOrderUseCase {

    private final CreateOrderActivity createOrderActivity;
    private final ValidateOrderActivity validateOrderActivity;
    private final ValidateProductOfferingsActivity validateProductOfferingsActivity;
    private final GetOrderActivity getOrderActivity;
    private final IdempotencyPort idempotencyPort;

    public CreateOrderUseCase(
            CreateOrderActivity createOrderActivity,
            ValidateOrderActivity validateOrderActivity,
            ValidateProductOfferingsActivity validateProductOfferingsActivity,
            GetOrderActivity getOrderActivity,
            IdempotencyPort idempotencyPort
    ) {
        this.createOrderActivity = createOrderActivity;
        this.validateOrderActivity = validateOrderActivity;
        this.validateProductOfferingsActivity = validateProductOfferingsActivity;
        this.getOrderActivity = getOrderActivity;
        this.idempotencyPort = idempotencyPort;
    }

    public CreateOrderResult execute(CreateOrderInput input) {
        if (input.idempotencyKey() != null && !input.idempotencyKey().isBlank()) {
            var existing = idempotencyPort.findByKey(input.idempotencyKey());
            if (existing.isPresent()) {
                if (!existing.get().payloadHash().equals(input.payloadHash())) {
                    throw new IdempotencyConflictException();
                }
                CustomerOrder order = getOrderActivity.execute(existing.get().orderId())
                        .orElseThrow(() -> new OrderNotFoundException(existing.get().orderId()));
                return new CreateOrderResult(order, true);
            }
        }

        validateOrderActivity.execute(input.orderItems(), input.paymentMethod());
        validateProductOfferingsActivity.execute(input.orderItems());

        CustomerOrder order = createOrderActivity.execute(input);
        if (input.idempotencyKey() != null && !input.idempotencyKey().isBlank()) {
            idempotencyPort.save(new IdempotencyRecord(input.idempotencyKey(), input.payloadHash(), order.id()));
        }
        return new CreateOrderResult(order, false);
    }
}
