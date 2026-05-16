package com.popbojan.takehome.order.adapters.driving.web;

import com.popbojan.takehome.order.domain.exception.CatalogUnavailableException;
import com.popbojan.takehome.order.domain.exception.DomainException;
import com.popbojan.takehome.order.domain.exception.IdempotencyConflictException;
import com.popbojan.takehome.order.domain.exception.InvalidOrderException;
import com.popbojan.takehome.order.domain.exception.InvalidStateTransitionException;
import com.popbojan.takehome.order.domain.exception.OrderNotFoundException;
import com.popbojan.takehome.order.domain.exception.UnknownProductOfferingException;
import com.popbojan.takehome.order.adapters.driving.web.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> notFound(OrderNotFoundException ex, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(IdempotencyConflictException.class)
    public ResponseEntity<ApiErrorResponse> idempotencyConflict(
            IdempotencyConflictException ex,
            HttpServletRequest request
    ) {
        return error(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler({
            InvalidOrderException.class,
            InvalidStateTransitionException.class,
            UnknownProductOfferingException.class
    })
    public ResponseEntity<ApiErrorResponse> domainValidation(DomainException ex, HttpServletRequest request) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
    }

    @ExceptionHandler(CatalogUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> catalogUnavailable(
            CatalogUnavailableException ex,
            HttpServletRequest request
    ) {
        return error(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> beanValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("Request validation failed");
        return error(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> badJson(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "Malformed or unsupported request body", request);
    }

    private ResponseEntity<ApiErrorResponse> error(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        ));
    }
}
