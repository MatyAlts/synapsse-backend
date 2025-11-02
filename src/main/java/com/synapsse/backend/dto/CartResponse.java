package com.synapsse.backend.dto;

import java.util.List;

public record CartResponse(
        Long id,
        List<CartItemResponse> items
) {}
