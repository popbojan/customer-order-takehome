package com.popbojan.takehome.order.adapters.driven.catalog;

import com.popbojan.takehome.order.domain.exception.CatalogUnavailableException;
import com.popbojan.takehome.order.domain.port.ProductCatalogPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class HttpProductCatalogAdapter implements ProductCatalogPort {

    private final RestClient restClient;

    public HttpProductCatalogAdapter(@Value("${catalog.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public boolean existsById(String productOfferingId) {
        try {
            restClient.get()
                    .uri("/product-offerings/{id}", productOfferingId)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            throw new CatalogUnavailableException("Catalog returned " + ex.getStatusCode());
        } catch (RestClientException ex) {
            throw new CatalogUnavailableException("Catalog is unavailable");
        }
    }
}
