package com.catalog.product;

import com.catalog.common.PageResponse;
import com.catalog.common.exception.ResourceNotFoundException;
import com.catalog.producer.Producer;
import com.catalog.producer.ProducerRepository;
import com.catalog.product.dto.CreateProductRequest;
import com.catalog.product.dto.ProductDto;
import com.catalog.product.dto.ProductFilter;
import com.catalog.product.dto.UpdateProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProducerRepository producerRepository;
    private final ProductMapper productMapper;

    @Override
    public PageResponse<ProductDto> findAll(ProductFilter filter, Pageable pageable) {
        Specification<Product> spec = ProductSpecification.withFilters(
                filter.name(), filter.producerId(), filter.minPrice(), filter.maxPrice());

        return PageResponse.from(
                productRepository.findAll(spec, pageable).map(productMapper::toDto)
        );
    }

    @Override
    public ProductDto findById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    @Override
    @Transactional
    public ProductDto create(CreateProductRequest request) {
        Producer producer = findProducerOrThrow(request.producerId());
        Product product = productMapper.toEntity(request, producer);
        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductDto update(Long id, UpdateProductRequest request) {
        Product product = findProductOrThrow(id);

        if (request.producerId() != null) {
            Producer producer = findProducerOrThrow(request.producerId());
            product.setProducer(producer);
        }
        productMapper.updateEntity(product, request);
        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", id);
        }
        productRepository.deleteById(id);
    }

    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    private Producer findProducerOrThrow(Long producerId) {
        return producerRepository.findById(producerId)
                .orElseThrow(() -> new ResourceNotFoundException("Producer", producerId));
    }
}
