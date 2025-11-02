package com.synapsse.backend.dto;

import java.math.BigDecimal;

public record ProductSummary(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String imageUrl
) {}
