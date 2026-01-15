package com.reactivo.tecnologia.domain.spi;

import com.reactivo.tecnologia.domain.model.Technology;
import com.reactivo.tecnologia.domain.model.TechnologySummary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TechnologyPersistencePort {
    Mono<Technology> save(Technology technology);

    Mono<Boolean> existByName(String name);

    Flux<Technology> findAll();

    Flux<TechnologySummary> findByIds(List<Long> ids);

    Mono<Technology> findById(Long id);

    Flux<Long> findOrphanedTechnologies(List<Long> capacityIds);

    Mono<Void> deleteTechnology(Long technologyId);

    Mono<Void> deleteCapacityTechnologyRelations(List<Long> capacityIds);

}
