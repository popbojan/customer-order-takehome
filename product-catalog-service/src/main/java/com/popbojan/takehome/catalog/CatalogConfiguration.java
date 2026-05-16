package com.popbojan.takehome.catalog;

import com.popbojan.takehome.catalog.domain.GetProductOfferingUseCase;
import com.popbojan.takehome.catalog.domain.ListProductOfferingsUseCase;
import com.popbojan.takehome.catalog.domain.activity.GetProductOfferingActivity;
import com.popbojan.takehome.catalog.domain.activity.ListProductOfferingsActivity;
import com.popbojan.takehome.catalog.domain.port.ProductOfferingPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CatalogConfiguration {

    @Bean
    GetProductOfferingUseCase getProductOfferingUseCase(ProductOfferingPort productOfferingPort) {
        return new GetProductOfferingUseCase(new GetProductOfferingActivity(productOfferingPort));
    }

    @Bean
    ListProductOfferingsUseCase listProductOfferingsUseCase(ProductOfferingPort productOfferingPort) {
        return new ListProductOfferingsUseCase(new ListProductOfferingsActivity(productOfferingPort));
    }
}
