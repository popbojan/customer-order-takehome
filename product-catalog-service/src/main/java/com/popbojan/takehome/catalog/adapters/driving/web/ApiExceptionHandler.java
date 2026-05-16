package com.popbojan.takehome.catalog.adapters.driving.web;

import com.popbojan.takehome.catalog.domain.exception.ProductOfferingNotFoundException;
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

    @ExceptionHandler(ProductOfferingNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> notFound(
            ProductOfferingNotFoundException exception, HttpServletRequest request
    ) {
        return jsonError(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> validation(
            MethodArgumentNotValidException exception, HttpServletRequest request
    ) {
        String message =
                exception.getBindingResult().getFieldErrors().stream().findFirst()
                        .map(error -> error.getField() + " " + error.getDefaultMessage()).orElse("Validation failed");
        return jsonError(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> badPayload(
            HttpMessageNotReadableException exception, HttpServletRequest request
    ) {
        return jsonError(HttpStatus.BAD_REQUEST, "Malformed or unsupported request body", request);
    }

    private ResponseEntity<ApiErrorResponse> jsonError(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ApiErrorResponse(
                Instant.now(), status.value(), status.getReasonPhrase(), message, request.getRequestURI()));
    }
}
