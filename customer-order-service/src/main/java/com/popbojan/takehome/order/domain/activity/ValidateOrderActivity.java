package com.popbojan.takehome.order.domain.activity;

import com.popbojan.takehome.order.domain.exception.InvalidOrderException;
import com.popbojan.takehome.order.domain.model.OrderItem;
import com.popbojan.takehome.order.domain.model.PaymentMethod;
import com.popbojan.takehome.order.domain.model.PaymentMethodType;
import java.util.List;

public class ValidateOrderActivity {

    public void execute(List<OrderItem> orderItems, PaymentMethod paymentMethod) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new InvalidOrderException("orderItems must not be empty");
        }

        for (OrderItem item : orderItems) {
            if (item.productOfferingId() == null || item.productOfferingId().isBlank()) {
                throw new InvalidOrderException("productOfferingId is required");
            }
            if (item.quantity() < 1) {
                throw new InvalidOrderException("quantity must be at least 1");
            }
        }

        if (paymentMethod == null || paymentMethod.type() == null) {
            throw new InvalidOrderException("paymentMethod.type is required");
        }
        if (paymentMethod.type() == PaymentMethodType.DIRECT_DEBIT
                && (paymentMethod.iban() == null || paymentMethod.iban().isBlank())) {
            throw new InvalidOrderException("iban is required for DIRECT_DEBIT");
        }
        if (paymentMethod.type() == PaymentMethodType.INVOICE && paymentMethod.iban() != null) {
            throw new InvalidOrderException("iban must be omitted for INVOICE");
        }
    }
}
