package com.reactivo.tecnologia.domain.spi;


import com.reactivo.tecnologia.domain.model.CapacityTechnology;
import com.reactivo.tecnologia.domain.model.TechnologySummary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface CapacityTechnologyPersistencePort {
    Flux<CapacityTechnology> saveAll(Flux<CapacityTechnology> capacityTechnologies);

    Flux<Long> findAllIdTechnologyByIdCapacity(Long idCapacity);

    Flux<CapacityTechnology> findByIdCapacityIn(List<Long> idCapacities);

    Mono<Map<Long, List<TechnologySummary>>> getCapacityIdGroupedTechnologiesAsMap(int page, int size, boolean asc);
}
