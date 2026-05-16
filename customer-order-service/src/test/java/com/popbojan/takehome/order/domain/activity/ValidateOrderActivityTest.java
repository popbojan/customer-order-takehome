package com.popbojan.takehome.order.domain.activity;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.popbojan.takehome.order.domain.exception.InvalidOrderException;
import com.popbojan.takehome.order.domain.model.OrderItem;
import com.popbojan.takehome.order.domain.model.PaymentMethod;
import com.popbojan.takehome.order.domain.model.PaymentMethodType;
import java.util.List;
import org.junit.jupiter.api.Test;

class ValidateOrderActivityTest {

    private final ValidateOrderActivity activity = new ValidateOrderActivity();

    @Test
    void acceptsInvoiceWithoutIban() {
        assertDoesNotThrow(() -> activity.execute(
                List.of(new OrderItem("po-1", 1)),
                new PaymentMethod(PaymentMethodType.INVOICE, null)
        ));
    }

    @Test
    void requiresIbanForDirectDebit() {
        assertThrows(InvalidOrderException.class, () -> activity.execute(
                List.of(new OrderItem("po-1", 1)),
                new PaymentMethod(PaymentMethodType.DIRECT_DEBIT, null)
        ));
    }

    @Test
    void rejectsEmptyItems() {
        assertThrows(InvalidOrderException.class, () -> activity.execute(
                List.of(),
                new PaymentMethod(PaymentMethodType.INVOICE, null)
        ));
    }
}
