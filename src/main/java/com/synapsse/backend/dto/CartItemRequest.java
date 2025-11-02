package com.synapsse.backend.dto;

public record CartItemRequest(
        Long productId,
        Integer quantity
) {}
