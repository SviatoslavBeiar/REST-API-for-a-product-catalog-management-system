package com.catalog.producer;

import com.catalog.common.exception.ResourceNotFoundException;
import com.catalog.producer.dto.CreateProducerRequest;
import com.catalog.producer.dto.ProducerDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProducerServiceTest {

    @Mock ProducerRepository producerRepository;
    @Mock ProducerMapper producerMapper;

    @InjectMocks ProducerServiceImpl producerService;

    @Test
    void create_whenNameIsUnique_shouldSave() {
        CreateProducerRequest request = new CreateProducerRequest("Samsung", "South Korea", null);
        Producer producer = Producer.builder().id(1L).name("Samsung").build();
        ProducerDto dto = new ProducerDto(1L, "Samsung", "South Korea", null, 0, null, null);

        when(producerRepository.existsByName("Samsung")).thenReturn(false);
        when(producerMapper.toEntity(request)).thenReturn(producer);
        when(producerRepository.save(producer)).thenReturn(producer);
        when(producerMapper.toDto(producer)).thenReturn(dto);

        ProducerDto result = producerService.create(request);

        assertThat(result.name()).isEqualTo("Samsung");
        verify(producerRepository).save(any());
    }

    @Test
    void create_whenNameAlreadyExists_shouldThrowIllegalArgumentException() {
        CreateProducerRequest request = new CreateProducerRequest("Samsung", null, null);
        when(producerRepository.existsByName("Samsung")).thenReturn(true);

        assertThatThrownBy(() -> producerService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Samsung");
    }

    @Test
    void findById_whenNotFound_shouldThrowResourceNotFoundException() {
        when(producerRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> producerService.findById(42L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("42");
    }
}
