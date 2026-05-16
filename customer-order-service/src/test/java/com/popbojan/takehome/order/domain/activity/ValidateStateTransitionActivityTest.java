package com.popbojan.takehome.order.domain.activity;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.popbojan.takehome.order.domain.exception.InvalidStateTransitionException;
import com.popbojan.takehome.order.domain.model.OrderState;
import org.junit.jupiter.api.Test;

class ValidateStateTransitionActivityTest {

    private final ValidateStateTransitionActivity activity = new ValidateStateTransitionActivity();

    @Test
    void allowsDefinedTransitions() {
        assertDoesNotThrow(() -> activity.execute(OrderState.DRAFT, OrderState.PREVIEW));
        assertDoesNotThrow(() -> activity.execute(OrderState.PREVIEW, OrderState.DRAFT));
        assertDoesNotThrow(() -> activity.execute(OrderState.PREVIEW, OrderState.SUBMITTED));
        assertDoesNotThrow(() -> activity.execute(OrderState.SUBMITTED, OrderState.CONFIRMED));
    }

    @Test
    void rejectsUndefinedTransitions() {
        assertThrows(InvalidStateTransitionException.class,
                () -> activity.execute(OrderState.DRAFT, OrderState.SUBMITTED));
        assertThrows(InvalidStateTransitionException.class,
                () -> activity.execute(OrderState.CONFIRMED, OrderState.DRAFT));
    }
}
