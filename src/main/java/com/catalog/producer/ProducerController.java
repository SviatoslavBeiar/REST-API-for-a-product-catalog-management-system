package com.catalog.producer;

import com.catalog.common.PageResponse;
import com.catalog.producer.dto.CreateProducerRequest;
import com.catalog.producer.dto.ProducerDto;
import com.catalog.producer.dto.UpdateProducerRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/producers")
@RequiredArgsConstructor
public class ProducerController {

    private final ProducerService producerService;

    @GetMapping
    public PageResponse<ProducerDto> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy
    ) {
        return producerService.findAll(PageRequest.of(page, size, Sort.by(sortBy)));
    }

    @GetMapping("/{id}")
    public ProducerDto getById(@PathVariable Long id) {
        return producerService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProducerDto create(@Valid @RequestBody CreateProducerRequest request) {
        return producerService.create(request);
    }

    @PatchMapping("/{id}")
    public ProducerDto update(@PathVariable Long id,
                              @Valid @RequestBody UpdateProducerRequest request) {
        return producerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        producerService.delete(id);
    }
}
