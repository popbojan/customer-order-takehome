package com.popbojan.takehome.order.adapters.driving.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.popbojan.takehome.order.adapters.driven.persistence.CustomerOrderRepository;
import com.popbojan.takehome.order.adapters.driven.persistence.IdempotencyRepository;
import com.popbojan.takehome.order.domain.port.ProductCatalogPort;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerOrderIntegrationTest {

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.datasource.url",
                () -> "jdbc:postgresql://" + integrationDbHost() + ":5434/orders");
        registry.add("spring.datasource.username", () -> "orders");
        registry.add("spring.datasource.password", () -> "orders");
        registry.add("catalog.base-url", () -> "http://catalog-not-used-in-tests");
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

    @Autowired
    CustomerOrderRepository orderRepository;

    @Autowired
    IdempotencyRepository idempotencyRepository;

    @MockBean
    ProductCatalogPort productCatalogPort;

    @BeforeEach
    void cleanDatabase() {
        restTemplate.getRestTemplate().setRequestFactory(new JdkClientHttpRequestFactory());
        idempotencyRepository.deleteAll();
        orderRepository.deleteAll();
        when(productCatalogPort.existsById(anyString())).thenReturn(true);
    }

    @Test
    void postCustomerOrdersCreatesDraftOrderAndPersistsIt() {
        var response = restTemplate.exchange(
                url("/customer-orders"),
                HttpMethod.POST,
                jsonEntity(validCreatePayload(), "create-key"),
                Map.class
        );

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getHeaders().getFirst("Idempotent-Replay")).isEqualTo("false");
        assertThat(response.getBody()).containsEntry("state", "draft");
        assertThat(response.getBody()).containsEntry("category", "B2C");
        assertThat(orderRepository.count()).isEqualTo(1);
        assertThat(idempotencyRepository.findById("create-key")).isPresent();
    }

    @Test
    void postCustomerOrdersReplaysSameIdempotencyKeyAndPayload() {
        var first = restTemplate.exchange(
                url("/customer-orders"),
                HttpMethod.POST,
                jsonEntity(validCreatePayload(), "same-key"),
                Map.class
        );

        var replay = restTemplate.exchange(
                url("/customer-orders"),
                HttpMethod.POST,
                jsonEntity(validCreatePayload(), "same-key"),
                Map.class
        );

        assertThat(first.getStatusCode().value()).isEqualTo(201);
        assertThat(replay.getStatusCode().value()).isEqualTo(200);
        assertThat(replay.getHeaders().getFirst("Idempotent-Replay")).isEqualTo("true");
        assertThat(replay.getBody()).containsEntry("id", first.getBody().get("id"));
        assertThat(orderRepository.count()).isEqualTo(1);
    }

    @Test
    void postCustomerOrdersRejectsSameIdempotencyKeyWithDifferentPayload() {
        restTemplate.exchange(
                url("/customer-orders"),
                HttpMethod.POST,
                jsonEntity(validCreatePayload(), "conflict-key"),
                Map.class
        );

        var differentPayload = validCreatePayload();
        differentPayload.put("customer", Map.of("id", "different-customer"));

        var conflict = restTemplate.exchange(
                url("/customer-orders"),
                HttpMethod.POST,
                jsonEntity(differentPayload, "conflict-key"),
                Map.class
        );

        assertThat(conflict.getStatusCode().value()).isEqualTo(409);
        assertThat(conflict.getBody()).containsEntry("error", "Conflict");
        assertThat(orderRepository.count()).isEqualTo(1);
    }

    @Test
    void patchCustomerOrdersMovesDraftToPreview() {
        var create = restTemplate.exchange(
                url("/customer-orders"),
                HttpMethod.POST,
                jsonEntity(validCreatePayload(), null),
                Map.class
        );
        var orderId = create.getBody().get("id");

        var patch = restTemplate.exchange(
                url("/customer-orders/" + orderId),
                HttpMethod.PATCH,
                jsonEntity(Map.of("state", "preview"), null),
                Map.class
        );

        assertThat(patch.getStatusCode().value()).isEqualTo(200);
        assertThat(patch.getBody()).containsEntry("state", "preview");
    }

    @Test
    void postCustomerOrdersRejectsUnknownProductOffering() {
        when(productCatalogPort.existsById(anyString())).thenReturn(false);

        var response = restTemplate.exchange(
                url("/customer-orders"),
                HttpMethod.POST,
                jsonEntity(validCreatePayload(), null),
                Map.class
        );

        assertThat(response.getStatusCode().value()).isEqualTo(422);
        assertThat(response.getBody().get("message").toString()).contains("Unknown product offering");
        assertThat(orderRepository.count()).isZero();
    }

    @Test
    void getCustomerOrdersReturnsPagedList() {
        restTemplate.exchange(url("/customer-orders"), HttpMethod.POST, jsonEntity(validCreatePayload(), null), Map.class);

        var response = restTemplate.getForEntity(url("/customer-orders?limit=20&offset=0&category=B2C"), Map.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsEntry("total", 1);
        assertThat((List<?>) response.getBody().get("items")).hasSize(1);
    }

    @Test
    void listCustomerOrdersWithMisalignedLimitAndOffset() {
        var idsNewestFirst = new java.util.ArrayDeque<String>();
        for (int index = 0; index < 8; index++) {
            Map<String, Object> payload = validCreatePayload();
            payload.put("customer", Map.of("id", "paging-cust-" + index));
            var created = restTemplate.exchange(
                    url("/customer-orders"),
                    HttpMethod.POST,
                    jsonEntity(payload, "paging-" + index),
                    Map.class);

            assertThat(created.getStatusCode().value()).isEqualTo(201);
            idsNewestFirst.addFirst(created.getBody().get("id").toString());
        }

        @SuppressWarnings("unchecked")
        Map<String, ?> page =
                restTemplate.getForEntity(url("/customer-orders?limit=3&offset=5"), Map.class).getBody();

        assertThat(page.get("total")).isEqualTo(8);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) page.get("items");

        List<String> actualIds = items.stream().map(m -> m.get("id").toString()).toList();
        List<String> expectedIds = idsNewestFirst.stream().skip(5).limit(3).toList();
        assertThat(actualIds).containsExactlyElementsOf(expectedIds);
    }

    @Test
    void getCustomerOrdersListSortedByCreatedAtDescending() throws Exception {
        for (int index = 0; index < 4; index++) {
            Map<String, Object> payload = validCreatePayload();
            payload.put("customer", Map.of("id", "sort-all-" + index));
            var created = restTemplate.exchange(
                    url("/customer-orders"),
                    HttpMethod.POST,
                    jsonEntity(payload, "sort-created-at-" + index),
                    Map.class);
            assertThat(created.getStatusCode().value()).isEqualTo(201);
            Thread.sleep(25);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> page = restTemplate.getForEntity(url("/customer-orders?limit=20&offset=0"), Map.class).getBody();

        assertThat(page.get("total")).isEqualTo(4);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) page.get("items");

        List<Instant> createdAts =
                items.stream().map(it -> Instant.parse(it.get("createdAt").toString())).toList();
        List<Instant> expectedOrder = createdAts.stream().sorted(Comparator.reverseOrder()).toList();
        assertThat(createdAts).containsExactlyElementsOf(expectedOrder);
    }

    @Test
    void getCustomerOrdersCategoryFilterPreservesCreatedAtDescending() throws Exception {
        Map<String, Object> firstB2c = validCreatePayload();
        firstB2c.put("customer", Map.of("id", "cat-sort-1"));
        restTemplate.exchange(
                url("/customer-orders"),
                HttpMethod.POST,
                jsonEntity(firstB2c, "cat-sort-1"),
                Map.class);

        Thread.sleep(25);

        Map<String, Object> b2b = validCreatePayload();
        b2b.put("category", "B2B");
        b2b.put("customer", Map.of("id", "cat-sort-b2b"));
        restTemplate.exchange(
                url("/customer-orders"), HttpMethod.POST, jsonEntity(b2b, "cat-sort-b2b"), Map.class);

        Thread.sleep(25);

        Map<String, Object> secondB2c = validCreatePayload();
        secondB2c.put("customer", Map.of("id", "cat-sort-2"));
        restTemplate.exchange(
                url("/customer-orders"),
                HttpMethod.POST,
                jsonEntity(secondB2c, "cat-sort-2"),
                Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> page =
                restTemplate.getForEntity(url("/customer-orders?limit=20&offset=0&category=B2C"), Map.class).getBody();

        assertThat(page.get("total")).isEqualTo(2);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) page.get("items");

        assertThat(items.get(0).get("customer")).isEqualTo(Map.of("id", "cat-sort-2"));
        assertThat(items.get(1).get("customer")).isEqualTo(Map.of("id", "cat-sort-1"));

        Instant newest = Instant.parse(items.get(0).get("createdAt").toString());
        Instant oldest = Instant.parse(items.get(1).get("createdAt").toString());
        assertThat(newest).isAfter(oldest);
    }

    private Map<String, Object> validCreatePayload() {
        return new java.util.LinkedHashMap<>(Map.of(
                "category", "B2C",
                "customer", Map.of("id", "cust-1"),
                "site", Map.of("id", "site-1"),
                "orderItems", List.of(Map.of("productOfferingId", "po-1", "quantity", 2)),
                "paymentMethod", Map.of("type", "INVOICE")
        ));
    }

    private HttpEntity<Map<String, Object>> jsonEntity(Map<String, Object> body, String idempotencyKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (idempotencyKey != null) {
            headers.set("Idempotency-Key", idempotencyKey);
        }
        return new HttpEntity<>(body, headers);
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
