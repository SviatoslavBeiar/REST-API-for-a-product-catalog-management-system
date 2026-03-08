package com.catalog.product.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Map;

public record UpdateProductRequest(

        @Size(max = 200, message = "Name must not exceed 200 characters")
        String name,

        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String description,

        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
        @Digits(integer = 13, fraction = 2, message = "Price must have at most 2 decimal places")
        BigDecimal price,

        Long producerId,

        Map<String, Object> attributes
) {}
