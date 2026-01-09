package com.reactivo.tecnologia.domain.api;

import com.reactivo.tecnologia.domain.model.Technology;
import reactor.core.publisher.Mono;

public interface TechnologyServicePort {
    Mono<Technology> saveTechnology(Technology technology);
}
