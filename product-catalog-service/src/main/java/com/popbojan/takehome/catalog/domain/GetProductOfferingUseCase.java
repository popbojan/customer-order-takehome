package com.popbojan.takehome.catalog.domain;

import com.popbojan.takehome.catalog.domain.activity.GetProductOfferingActivity;
import com.popbojan.takehome.catalog.domain.model.ProductOffering;
import java.util.Optional;

public class GetProductOfferingUseCase {

    private final GetProductOfferingActivity getProductOfferingActivity;

    public GetProductOfferingUseCase(GetProductOfferingActivity getProductOfferingActivity) {
        this.getProductOfferingActivity = getProductOfferingActivity;
    }

    public Optional<ProductOffering> execute(String id) {
        return getProductOfferingActivity.execute(id);
    }
}
