package com.popbojan.takehome.catalog.adapters.driven.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOfferingRepository extends JpaRepository<ProductOfferingEntity, String> {
}
