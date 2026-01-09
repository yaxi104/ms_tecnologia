package com.reactivo.tecnologia.domain.api;

import com.reactivo.tecnologia.domain.model.CapacityTechnology;
import com.reactivo.tecnologia.domain.model.TechnologySummary;
import reactor.core.publisher.Flux;

public interface CapacityTechnologyServicePort {
    Flux<CapacityTechnology> saveAllCapacityTechnology(Flux<CapacityTechnology> capacityTechnologies);

    Flux<TechnologySummary> findAllIdTechnologyByIdCapacity(Long idCapacity);
}
