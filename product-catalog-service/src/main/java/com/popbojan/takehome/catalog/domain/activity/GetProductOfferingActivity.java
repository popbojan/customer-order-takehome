package com.popbojan.takehome.catalog.domain.activity;

import com.popbojan.takehome.catalog.domain.model.ProductOffering;
import com.popbojan.takehome.catalog.domain.port.ProductOfferingPort;
import java.util.Optional;

public class GetProductOfferingActivity {

    private final ProductOfferingPort productOfferingPort;

    public GetProductOfferingActivity(ProductOfferingPort productOfferingPort) {
        this.productOfferingPort = productOfferingPort;
    }

    public Optional<ProductOffering> execute(String id) {
        return productOfferingPort.findById(id);
    }
}
