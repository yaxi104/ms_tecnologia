package com.reactivo.tecnologia.domain.api;

import com.reactivo.tecnologia.domain.model.CapacityTechnology;
import com.reactivo.tecnologia.domain.model.TechnologySummary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface CapacityTechnologyServicePort {
    Flux<CapacityTechnology> saveAllCapacityTechnology(Flux<CapacityTechnology> capacityTechnologies);

    Flux<TechnologySummary> findAllIdTechnologyByIdCapacity(Long idCapacity);

    Mono<Map<Long, List<TechnologySummary>>> findTechnologiesByCapacityIds(List<Long> capacityIds);

}
