package com.catalog.product.dto;

import java.math.BigDecimal;

public record ProductFilter(
        String name,
        Long producerId,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {
    public static ProductFilter empty() {
        return new ProductFilter(null, null, null, null);
    }
}
