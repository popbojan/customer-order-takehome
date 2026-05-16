package com.popbojan.takehome.catalog.domain.activity;

import com.popbojan.takehome.catalog.domain.model.ProductOffering;
import com.popbojan.takehome.catalog.domain.port.ProductOfferingPort;
import java.util.List;

public class ListProductOfferingsActivity {

    private final ProductOfferingPort productOfferingPort;

    public ListProductOfferingsActivity(ProductOfferingPort productOfferingPort) {
        this.productOfferingPort = productOfferingPort;
    }

    public List<ProductOffering> execute() {
        return productOfferingPort.findAll();
    }
}
