package com.catalog.producer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateProducerRequest(

        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @Size(max = 100, message = "Country must not exceed 100 characters")
        String country,

        @Email(message = "Contact email must be a valid email address")
        @Size(max = 150)
        String contactEmail
) {}
