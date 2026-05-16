package com.popbojan.takehome.catalog.adapters.driven.persistence;

import com.popbojan.takehome.catalog.domain.model.ProductOffering;
import com.popbojan.takehome.catalog.domain.port.ProductOfferingPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class JpaProductOfferingAdapter implements ProductOfferingPort {

    private final ProductOfferingRepository repository;

    public JpaProductOfferingAdapter(ProductOfferingRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<ProductOffering> findById(String id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    @Override
    public List<ProductOffering> findAll() {
        return repository.findAll().stream().map(this::mapToDomain).toList();
    }

    private ProductOffering mapToDomain(ProductOfferingEntity entity) {
        return new ProductOffering(entity.getId(), entity.getName(), entity.getPrice());
    }
}
