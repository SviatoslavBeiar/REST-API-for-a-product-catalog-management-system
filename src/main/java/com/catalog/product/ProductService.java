package com.catalog.product;

import com.catalog.common.PageResponse;
import com.catalog.product.dto.CreateProductRequest;
import com.catalog.product.dto.ProductDto;
import com.catalog.product.dto.ProductFilter;
import com.catalog.product.dto.UpdateProductRequest;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    PageResponse<ProductDto> findAll(ProductFilter filter, Pageable pageable);

    ProductDto findById(Long id);

    ProductDto create(CreateProductRequest request);

    ProductDto update(Long id, UpdateProductRequest request);

    void delete(Long id);
}
