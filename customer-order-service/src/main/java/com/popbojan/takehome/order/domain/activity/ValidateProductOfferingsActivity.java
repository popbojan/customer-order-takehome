package com.popbojan.takehome.order.domain.activity;

import com.popbojan.takehome.order.domain.exception.UnknownProductOfferingException;
import com.popbojan.takehome.order.domain.model.OrderItem;
import com.popbojan.takehome.order.domain.port.ProductCatalogPort;
import java.util.List;

public class ValidateProductOfferingsActivity {

    private final ProductCatalogPort productCatalogPort;

    public ValidateProductOfferingsActivity(ProductCatalogPort productCatalogPort) {
        this.productCatalogPort = productCatalogPort;
    }

    public void execute(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            if (!productCatalogPort.existsById(item.productOfferingId())) {
                throw new UnknownProductOfferingException(item.productOfferingId());
            }
        }
    }
}
