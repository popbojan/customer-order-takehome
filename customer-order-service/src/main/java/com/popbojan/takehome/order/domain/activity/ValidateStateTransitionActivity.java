package com.popbojan.takehome.order.domain.activity;

import com.popbojan.takehome.order.domain.exception.InvalidStateTransitionException;
import com.popbojan.takehome.order.domain.model.OrderState;

public class ValidateStateTransitionActivity {

    public void execute(OrderState current, OrderState next) {
        if (current == next) {
            return;
        }
        if (current == OrderState.DRAFT && next == OrderState.PREVIEW) {
            return;
        }
        if (current == OrderState.PREVIEW && (next == OrderState.DRAFT || next == OrderState.SUBMITTED)) {
            return;
        }
        if (current == OrderState.SUBMITTED && next == OrderState.CONFIRMED) {
            return;
        }

        throw new InvalidStateTransitionException(
                "Invalid order state transition from " + current.apiValue() + " to " + next.apiValue()
        );
    }
}
