package com.popbojan.takehome.catalog.domain.port;

import com.popbojan.takehome.catalog.domain.model.ProductOffering;
import java.util.List;
import java.util.Optional;

public interface ProductOfferingPort {

    Optional<ProductOffering> findById(String id);

    List<ProductOffering> findAll();
}
