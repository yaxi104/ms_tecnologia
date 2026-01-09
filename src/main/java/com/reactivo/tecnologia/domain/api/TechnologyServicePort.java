package com.reactivo.tecnologia.domain.api;

import com.reactivo.tecnologia.domain.model.Technology;
import com.reactivo.tecnologia.domain.model.TechnologySummary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TechnologyServicePort {
    Mono<Technology> saveTechnology(Technology technology);

    Flux<Technology> findAll();

    Flux<TechnologySummary> findByIds(List<Long> ids);

}
