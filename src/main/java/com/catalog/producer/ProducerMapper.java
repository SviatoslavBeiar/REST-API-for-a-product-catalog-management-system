package com.catalog.producer;

import com.catalog.producer.dto.CreateProducerRequest;
import com.catalog.producer.dto.ProducerDto;
import com.catalog.producer.dto.UpdateProducerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProducerMapper {

    private final ProducerRepository producerRepository;

    public ProducerDto toDto(Producer producer) {

        int productCount = producerRepository.countProductsByProducerId(producer.getId());

        return new ProducerDto(
                producer.getId(),
                producer.getName(),
                producer.getCountry(),
                producer.getContactEmail(),
                productCount,
                producer.getCreatedAt(),
                producer.getUpdatedAt()
        );
    }

    public Producer toEntity(CreateProducerRequest request) {
        return Producer.builder()
                .name(request.name())
                .country(request.country())
                .contactEmail(request.contactEmail())
                .build();
    }

    public void updateEntity(Producer producer, UpdateProducerRequest request) {
        if (request.name() != null)         producer.setName(request.name());
        if (request.country() != null)      producer.setCountry(request.country());
        if (request.contactEmail() != null) producer.setContactEmail(request.contactEmail());
    }
}
