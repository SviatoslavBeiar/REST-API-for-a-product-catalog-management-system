package com.catalog.producer;

import com.catalog.common.PageResponse;
import com.catalog.producer.dto.CreateProducerRequest;
import com.catalog.producer.dto.ProducerDto;
import com.catalog.producer.dto.UpdateProducerRequest;
import org.springframework.data.domain.Pageable;

public interface ProducerService {

    PageResponse<ProducerDto> findAll(Pageable pageable);

    ProducerDto findById(Long id);

    ProducerDto create(CreateProducerRequest request);

    ProducerDto update(Long id, UpdateProducerRequest request);

    void delete(Long id);
}
