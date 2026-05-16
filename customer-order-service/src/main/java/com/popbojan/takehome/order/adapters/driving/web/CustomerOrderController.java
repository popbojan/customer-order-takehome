package com.popbojan.takehome.order.adapters.driving.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.popbojan.takehome.order.domain.CreateOrderUseCase;
import com.popbojan.takehome.order.domain.GetOrderUseCase;
import com.popbojan.takehome.order.domain.ListOrdersUseCase;
import com.popbojan.takehome.order.domain.PatchOrderUseCase;
import com.popbojan.takehome.order.domain.exception.OrderNotFoundException;
import com.popbojan.takehome.order.domain.model.OrderCategory;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer-orders")
public class CustomerOrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final ListOrdersUseCase listOrdersUseCase;
    private final PatchOrderUseCase patchOrderUseCase;
    private final OrderMapper orderMapper = new OrderMapper();
    private final ObjectMapper canonicalMapper;

    public CustomerOrderController(
            CreateOrderUseCase createOrderUseCase,
            GetOrderUseCase getOrderUseCase,
            ListOrdersUseCase listOrdersUseCase,
            PatchOrderUseCase patchOrderUseCase
    ) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.listOrdersUseCase = listOrdersUseCase;
        this.patchOrderUseCase = patchOrderUseCase;
        this.canonicalMapper = new ObjectMapper()
                .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
                .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        var input = orderMapper.mapToInput(request, idempotencyKey, payloadHash(request));
        var result = createOrderUseCase.execute(input);

        return ResponseEntity.status(result.replayed() ? HttpStatus.OK : HttpStatus.CREATED)
                .header("Idempotent-Replay", Boolean.toString(result.replayed()))
                .body(orderMapper.mapToResponse(result.order()));
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable("id") UUID id) {
        return getOrderUseCase.execute(id)
                .map(orderMapper::mapToResponse)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @GetMapping
    public PageResponse<OrderResponse> list(
            @RequestParam(value = "category", required = false) OrderCategory category,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset
    ) {
        var page = listOrdersUseCase.execute(category, limit, offset);
        return new PageResponse<>(
                page.items().stream().map(orderMapper::mapToResponse).toList(),
                page.total(),
                page.limit(),
                page.offset()
        );
    }

    @PatchMapping("/{id}")
    public OrderResponse patch(@PathVariable("id") UUID id, @RequestBody PatchOrderRequest request) {
        return orderMapper.mapToResponse(patchOrderUseCase.execute(orderMapper.mapToInput(id, request)));
    }

    private String payloadHash(CreateOrderRequest request) {
        try {
            String canonicalPayload = canonicalMapper.writeValueAsString(request);
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(canonicalPayload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (JsonProcessingException | NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Could not hash request payload", ex);
        }
    }
}
