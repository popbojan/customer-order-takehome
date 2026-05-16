# Customer Order Take-Home

Two Java 21 / Spring Boot 3 services:

- **Customer Order Service** on `http://localhost:8080`
- **Product Catalog Service** on `http://localhost:8081`

Both services use Postgres in Docker and Flyway migrations. The catalog is seeded with `po-1`, `po-2`, and `po-3`.

## How to Run

```bash
docker compose up --build
```

The compose file starts:

- `catalog-db` Postgres, exposed on host port `5433`
- `order-db` Postgres, exposed on host port `5434`
- `product-catalog-service`, exposed on `8081` (waits until `/actuator/health` succeeds)
- `customer-order-service`, exposed on `8080` (starts only after the catalog **and** databases are healthy)

No extra environment variables are required for the Docker path.

Actuator **`/actuator/health`** is exposed on **8080** and **8081** for Docker Compose health checks (the images install `curl` for this).

## Quick Smoke Test

Catalog lookup:

```bash
curl http://localhost:8081/product-offerings/po-1
```

Create an order:

```bash
curl -i -X POST http://localhost:8080/customer-orders \
  -H 'Content-Type: application/json' \
  -H 'Idempotency-Key: demo-key' \
  -d '{
    "category": "B2C",
    "customer": { "id": "cust-1" },
    "site": { "id": "site-1" },
    "orderItems": [
      { "productOfferingId": "po-1", "quantity": 2 }
    ],
    "paymentMethod": { "type": "INVOICE" }
  }'
```

Replay the same request with the same `Idempotency-Key`; it returns the same order with:

```text
Idempotent-Replay: true
```

Patch state:

```bash
curl -X PATCH http://localhost:8080/customer-orders/{id} \
  -H 'Content-Type: application/json' \
  -d '{ "state": "preview" }'
```

List orders:

```bash
curl 'http://localhost:8080/customer-orders?limit=20&offset=0&category=B2C'
```

## What Was Built

The implementation covers the requested functional surface:

- Create, retrieve, list, and partially update customer orders.
- Order lifecycle: `draft -> preview`, `preview -> draft`, `preview -> submitted`, `submitted -> confirmed`.
- Payload edits are blocked after `submitted`; all changes are blocked after `confirmed`.
- Product offering references are validated against the catalog service on create and when `orderItems` are patched.
- Idempotent create using `Idempotency-Key`.
- Consistent JSON error body for validation, rule violations, conflicts, not found, and catalog unavailability.
- Catalog service with product offering lookup and a small list endpoint.

## Architecture

The code follows the same Ports & Adapters style as my task-manager backend:

```text
domain/
  model/
  port/
  activity/
  *UseCase.java
adapters/
  driving/web/
  driven/persistence/
  driven/catalog/
```

The domain owns business rules and talks to interfaces (`CustomerOrderPort`, `IdempotencyPort`, `ProductCatalogPort`, `OrderCreateTxnPort`). Spring MVC, JPA, and HTTP clients live in adapters. **`RunCreateOrderTransactionActivity`** invokes **`OrderCreateTxnPort`**; **`OrderCreateRepositoryService`** (`adapters/driven/persistence`) implements it with **`@Transactional persistInTransaction`**, grouping Postgres advisory locking, inserts, and idempotency in one JDBC transaction. `OrderConfiguration` and `CatalogConfiguration` wire activities and use cases together.

## Decisions and Tradeoffs

### State Machine

State transitions are explicit in `ValidateStateTransitionActivity`. This keeps lifecycle rules close to the domain and makes invalid transitions fail before persistence. Re-applying the same state is accepted as a no-op because JSON Merge Patch clients sometimes resend current values.

### PATCH Semantics

PATCH behaves like JSON Merge Patch for supported fields: present fields replace existing values, absent fields are unchanged. For nested objects (`customer`, `site`, `paymentMethod`) this implementation replaces the nested value rather than doing deep per-property merging. That keeps behavior predictable for the exercise.

### Idempotency

Create stores `idempotency_key`, a SHA-256 hash of the canonical request body, and the created `order_id`.

- Same key + same payload returns the original order with `200 OK` and `Idempotent-Replay: true`.
- Same key + different payload returns `409 Conflict`.
- Fresh create returns `201 Created` and `Idempotent-Replay: false`.

Create runs in **one Spring transaction** in **`OrderCreateRepositoryService#persistInTransaction`** (implements **`OrderCreateTxnPort`**), entered via **`RunCreateOrderTransactionActivity`**. For requests that send an idempotency key, the flow acquires a **Postgres advisory transaction lock** keyed by that header so concurrent creates with the same key cannot observe a stale "empty" row and insert duplicate orders. This is Postgres-specific.

### Pagination

`/customer-orders` list paging uses **`limit`** and **`offset`** with **`ORDER BY created_at DESC`**, mapped through JPA `setFirstResult` / `setMaxResults` so **any** offset aligns with Spring Data semantics (pages are **not** approximated via `page = offset/limit`).

### Catalog Communication

The order service uses HTTP (`GET /product-offerings/{id}`) through a driven adapter. A `404` means the product offering is unknown and the order request is rejected with `422`. Other catalog failures are treated as `503 Service Unavailable`; I chose fail-closed because accepting an order with unknown product references would violate ownership boundaries.

The catalog REST layer returns the **same `{ timestamp, status, error, message, path }` JSON** for predictable failures (`404`, invalid JSON payloads, bean validation hits) via `ApiExceptionHandler`.

### Persistence

Each service owns its own Postgres database. Flyway creates schemas and seeds the catalog. JPA is used for persistence, but Hibernate schema generation is disabled (`ddl-auto: validate`) so the runtime schema is migration-driven.

### Error Shape

Errors use one consistent response shape:

```json
{
  "timestamp": "2026-05-16T18:00:00Z",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Unknown product offering: po-x",
  "path": "/customer-orders"
}
```

## Known Limitations

- Postgres-specific advisory locks serialize concurrent creates sharing the same `Idempotency-Key`; a different DB would need another mutex strategy.
- The catalog exposes listing only as a convenience; the required contract is lookup by id.
- PATCH does replacement for nested structures instead of full deep merge.
- Docker image builds skip tests for faster `docker compose up`; run tests separately with Maven if desired.
- There is no OpenAPI document in this submission. The API is intentionally small and documented here.

## Tests

There are focused domain tests for state transitions and order validation in `customer-order-service/src/test`.

Integration tests expect **Postgres** from Docker Compose on the published ports (`5433` catalog, `5434` orders). Start the databases first:

```bash
docker compose up -d catalog-db order-db
```

From the repository root, run **all** modules (parent POM):

```bash
./mvnw test
```

Other useful commands:

```bash
./mvnw clean package    # runs tests, then packages both services
./mvnw -pl customer-order-service test
```

If you run Maven **inside** a Linux container and Postgres runs on the host, the DB host is not `localhost` from the container’s view. On macOS/Windows Docker Desktop you can use:

```bash
docker run --rm -e INTEGRATION_TEST_DB_HOST=host.docker.internal -v "$PWD":/workspace -w /workspace maven:3.9-eclipse-temurin-21 ./mvnw test
```
