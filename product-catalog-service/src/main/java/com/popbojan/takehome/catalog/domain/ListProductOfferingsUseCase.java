package com.popbojan.takehome.catalog.domain;

import com.popbojan.takehome.catalog.domain.activity.ListProductOfferingsActivity;
import com.popbojan.takehome.catalog.domain.model.ProductOffering;
import java.util.List;

public class ListProductOfferingsUseCase {

    private final ListProductOfferingsActivity listProductOfferingsActivity;

    public ListProductOfferingsUseCase(ListProductOfferingsActivity listProductOfferingsActivity) {
        this.listProductOfferingsActivity = listProductOfferingsActivity;
    }

    public List<ProductOffering> execute() {
        return listProductOfferingsActivity.execute();
    }
}
