package com.catalog.product;

import com.catalog.common.PageResponse;
import com.catalog.common.exception.ResourceNotFoundException;
import com.catalog.producer.Producer;
import com.catalog.producer.ProducerRepository;
import com.catalog.product.dto.CreateProductRequest;
import com.catalog.product.dto.ProductDto;
import com.catalog.product.dto.ProductFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock ProductRepository productRepository;
    @Mock ProducerRepository producerRepository;
    @Mock ProductMapper productMapper;

    @InjectMocks ProductServiceImpl productService;

    private Producer samsung;
    private Product tv;

    @BeforeEach
    void setUp() {
        samsung = Producer.builder().id(1L).name("Samsung").country("South Korea").build();
        tv = Product.builder()
                .id(1L).name("Samsung TV").price(new BigDecimal("999.99"))
                .producer(samsung).attributes(Map.of("screen_size_inch", 55))
                .build();
    }

    @Test
    void findAll_shouldReturnPagedProducts() {
        ProductDto dto = mockProductDto();
        when(productRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(tv)));
        when(productMapper.toDto(tv)).thenReturn(dto);

        PageResponse<ProductDto> result = productService.findAll(ProductFilter.empty(), PageRequest.of(0, 20));

        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void findById_whenExists_shouldReturnDto() {
        ProductDto dto = mockProductDto();
        when(productRepository.findById(1L)).thenReturn(Optional.of(tv));
        when(productMapper.toDto(tv)).thenReturn(dto);

        ProductDto result = productService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Samsung TV");
    }

    @Test
    void findById_whenNotFound_shouldThrowResourceNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_whenProducerExists_shouldSaveAndReturnDto() {
        CreateProductRequest request = new CreateProductRequest(
                "Samsung TV", "A great TV", new BigDecimal("999.99"), 1L, Map.of());
        ProductDto dto = mockProductDto();

        when(producerRepository.findById(1L)).thenReturn(Optional.of(samsung));
        when(productMapper.toEntity(request, samsung)).thenReturn(tv);
        when(productRepository.save(tv)).thenReturn(tv);
        when(productMapper.toDto(tv)).thenReturn(dto);

        ProductDto result = productService.create(request);

        assertThat(result.name()).isEqualTo("Samsung TV");
        verify(productRepository).save(tv);
    }

    @Test
    void create_whenProducerNotFound_shouldThrowResourceNotFoundException() {
        CreateProductRequest request = new CreateProductRequest(
                "TV", "desc", new BigDecimal("100"), 99L, null);
        when(producerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_whenNotFound_shouldThrowResourceNotFoundException() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> productService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private ProductDto mockProductDto() {
        return new ProductDto(1L, "Samsung TV", "A great TV", new BigDecimal("999.99"),
                new ProductDto.ProducerSummary(1L, "Samsung", "South Korea"),
                Map.of("screen_size_inch", 55), null, null);
    }
}
