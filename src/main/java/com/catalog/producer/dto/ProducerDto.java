package com.catalog.producer.dto;

import java.time.LocalDateTime;

public record ProducerDto(
        Long id,
        String name,
        String country,
        String contactEmail,
        int productCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
