package com.reactivo.tecnologia.domain.spi;

import com.reactivo.tecnologia.domain.model.CapacityTechnology;
import com.reactivo.tecnologia.domain.model.CapacityTechnologyGroup;
import reactor.core.publisher.Flux;

import java.util.List;

public interface CapacityTechnologyPersistencePort {
    Flux<CapacityTechnology> saveAll(Flux<CapacityTechnology> capacityTechnologies);

    Flux<Long> findAllIdTechnologyByIdCapacity(Long idCapacity);

    Flux<CapacityTechnology> findByIdCapacityIn(List<Long> idCapacities);

    Flux<CapacityTechnology> findByIdCapacityInPaged(int page, int size, boolean asc);

    Flux<CapacityTechnologyGroup> findPaged(int page, int size, boolean asc);
}
