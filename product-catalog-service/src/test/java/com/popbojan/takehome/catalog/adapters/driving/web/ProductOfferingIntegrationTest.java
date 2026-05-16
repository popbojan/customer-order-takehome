package com.popbojan.takehome.catalog.adapters.driving.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductOfferingIntegrationTest {

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.datasource.url",
                () -> "jdbc:postgresql://" + integrationDbHost() + ":5433/catalog");
        registry.add("spring.datasource.username", () -> "catalog");
        registry.add("spring.datasource.password", () -> "catalog");
    }

    /** {@code localhost} for host runs; set {@code INTEGRATION_TEST_DB_HOST} when Maven runs inside Docker. */
    private static String integrationDbHost() {
        String env = System.getenv("INTEGRATION_TEST_DB_HOST");
        return (env != null && !env.isBlank()) ? env : "localhost";
    }

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void getProductOfferingReturnsSeededOffering() {
        var response = restTemplate.getForEntity(url("/product-offerings/po-1"), ProductOfferingResponse.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo("po-1");
        assertThat(response.getBody().name()).isEqualTo("Fiber Internet 100");
        assertThat(response.getBody().price()).isEqualByComparingTo("29.99");
    }

    @Test
    void getProductOfferingReturns404ForUnknownOffering() {
        var response = restTemplate.getForEntity(url("/product-offerings/does-not-exist"), String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void listProductOfferingsReturnsSeededOfferings() {
        var response = restTemplate.getForEntity(url("/product-offerings"), List.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(3);
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
