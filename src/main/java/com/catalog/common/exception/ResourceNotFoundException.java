package com.catalog.common.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super("%s with id %d not found".formatted(resourceName, id));
    }
}
