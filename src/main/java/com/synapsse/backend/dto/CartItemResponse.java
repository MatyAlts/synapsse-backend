package com.synapsse.backend.dto;

public record CartItemResponse(
        Long id,
        ProductSummary product,
        Integer quantity
) {}
