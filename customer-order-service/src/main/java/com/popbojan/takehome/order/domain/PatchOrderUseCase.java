package com.popbojan.takehome.order.domain;

import com.popbojan.takehome.order.domain.activity.GetOrderActivity;
import com.popbojan.takehome.order.domain.activity.UpdateOrderActivity;
import com.popbojan.takehome.order.domain.activity.ValidateOrderActivity;
import com.popbojan.takehome.order.domain.activity.ValidateProductOfferingsActivity;
import com.popbojan.takehome.order.domain.activity.ValidateStateTransitionActivity;
import com.popbojan.takehome.order.domain.exception.InvalidStateTransitionException;
import com.popbojan.takehome.order.domain.exception.OrderNotFoundException;
import com.popbojan.takehome.order.domain.model.CustomerOrder;
import com.popbojan.takehome.order.domain.model.OrderState;
import com.popbojan.takehome.order.domain.model.PatchOrderInput;
import java.time.Instant;

public class PatchOrderUseCase {

    private final GetOrderActivity getOrderActivity;
    private final UpdateOrderActivity updateOrderActivity;
    private final ValidateOrderActivity validateOrderActivity;
    private final ValidateProductOfferingsActivity validateProductOfferingsActivity;
    private final ValidateStateTransitionActivity validateStateTransitionActivity;

    public PatchOrderUseCase(
            GetOrderActivity getOrderActivity,
            UpdateOrderActivity updateOrderActivity,
            ValidateOrderActivity validateOrderActivity,
            ValidateProductOfferingsActivity validateProductOfferingsActivity,
            ValidateStateTransitionActivity validateStateTransitionActivity
    ) {
        this.getOrderActivity = getOrderActivity;
        this.updateOrderActivity = updateOrderActivity;
        this.validateOrderActivity = validateOrderActivity;
        this.validateProductOfferingsActivity = validateProductOfferingsActivity;
        this.validateStateTransitionActivity = validateStateTransitionActivity;
    }

    public CustomerOrder execute(PatchOrderInput input) {
        CustomerOrder current = getOrderActivity.execute(input.id())
                .orElseThrow(() -> new OrderNotFoundException(input.id()));

        if (current.state() == OrderState.CONFIRMED) {
            throw new InvalidStateTransitionException("Confirmed orders cannot be changed");
        }
        if (current.state() == OrderState.SUBMITTED && input.changesPayloadFields()) {
            throw new InvalidStateTransitionException("Submitted orders allow only state changes");
        }

        var nextState = input.state().orElse(current.state());
        validateStateTransitionActivity.execute(current.state(), nextState);

        var nextItems = input.orderItems().orElse(current.orderItems());
        var nextPayment = input.paymentMethod().orElse(current.paymentMethod());
        validateOrderActivity.execute(nextItems, nextPayment);

        if (input.orderItems().isPresent()) {
            validateProductOfferingsActivity.execute(nextItems);
        }

        CustomerOrder next = new CustomerOrder(
                current.id(),
                nextState,
                input.category().orElse(current.category()),
                input.customerId().orElse(current.customerId()),
                input.siteId().orElse(current.siteId()),
                nextItems,
                nextPayment,
                current.createdAt(),
                Instant.now()
        );

        return updateOrderActivity.execute(next);
    }
}
