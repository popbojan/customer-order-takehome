package com.popbojan.takehome.catalog.adapters.driving.web;

import com.popbojan.takehome.catalog.domain.GetProductOfferingUseCase;
import com.popbojan.takehome.catalog.domain.ListProductOfferingsUseCase;
import com.popbojan.takehome.catalog.domain.model.ProductOffering;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product-offerings")
public class ProductOfferingController {

    private final GetProductOfferingUseCase getProductOfferingUseCase;
    private final ListProductOfferingsUseCase listProductOfferingsUseCase;

    public ProductOfferingController(
            GetProductOfferingUseCase getProductOfferingUseCase,
            ListProductOfferingsUseCase listProductOfferingsUseCase
    ) {
        this.getProductOfferingUseCase = getProductOfferingUseCase;
        this.listProductOfferingsUseCase = listProductOfferingsUseCase;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductOfferingResponse> getById(@PathVariable("id") String id) {
        return getProductOfferingUseCase.execute(id)
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ProductOfferingResponse> list() {
        return listProductOfferingsUseCase.execute().stream().map(this::mapToResponse).toList();
    }

    private ProductOfferingResponse mapToResponse(ProductOffering offering) {
        return new ProductOfferingResponse(offering.id(), offering.name(), offering.price());
    }
}
