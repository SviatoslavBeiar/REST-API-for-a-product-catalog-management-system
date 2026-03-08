package com.catalog.product.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public record ProductDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        ProducerSummary producer,
        Map<String, Object> attributes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public record ProducerSummary(Long id, String name, String country) {}
}
