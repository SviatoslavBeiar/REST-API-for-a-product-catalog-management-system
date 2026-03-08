package com.catalog.product;

import com.catalog.producer.Producer;
import com.catalog.producer.dto.ProducerDto;
import com.catalog.product.dto.CreateProductRequest;
import com.catalog.product.dto.ProductDto;
import com.catalog.product.dto.UpdateProductRequest;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;

@Component
public class ProductMapper {

    public ProductDto toDto(Product product) {
        Producer producer = product.getProducer();
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                new ProductDto.ProducerSummary(producer.getId(), producer.getName(), producer.getCountry()),
                product.getAttributes(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    public Product toEntity(CreateProductRequest request, Producer producer) {
        return Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .producer(producer)
                .attributes(request.attributes() != null ? new HashMap<>(request.attributes()) : new HashMap<>())
                .build();
    }

    public void updateEntity(Product product, UpdateProductRequest request) {
        if (request.name() != null)        product.setName(request.name());
        if (request.description() != null) product.setDescription(request.description());
        if (request.price() != null)       product.setPrice(request.price());
        if (request.attributes() != null)  product.setAttributes(new HashMap<>(request.attributes()));
    }
}
