package com.catalog.producer;

import com.catalog.common.PageResponse;
import com.catalog.common.exception.ResourceNotFoundException;
import com.catalog.producer.dto.CreateProducerRequest;
import com.catalog.producer.dto.ProducerDto;
import com.catalog.producer.dto.UpdateProducerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProducerServiceImpl implements ProducerService {

    private final ProducerRepository producerRepository;
    private final ProducerMapper producerMapper;

    @Override
    public PageResponse<ProducerDto> findAll(Pageable pageable) {
        return PageResponse.from(
                producerRepository.findAll(pageable).map(producerMapper::toDto)
        );
    }

    @Override
    public ProducerDto findById(Long id) {
        return producerRepository.findById(id)
                .map(producerMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Producer", id));
    }

    @Override
    @Transactional
    public ProducerDto create(CreateProducerRequest request) {
        validateUniqueName(request.name());
        Producer producer = producerMapper.toEntity(request);
        return producerMapper.toDto(producerRepository.save(producer));
    }

    @Override
    @Transactional
    public ProducerDto update(Long id, UpdateProducerRequest request) {
        Producer producer = findProducerOrThrow(id);
        producerMapper.updateEntity(producer, request);
        return producerMapper.toDto(producerRepository.save(producer));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!producerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producer", id);
        }
        producerRepository.deleteById(id);
    }

    private Producer findProducerOrThrow(Long id) {
        return producerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producer", id));
    }

    private void validateUniqueName(String name) {
        if (producerRepository.existsByName(name)) {
            throw new IllegalArgumentException("Producer with name '%s' already exists".formatted(name));
        }
    }
}
