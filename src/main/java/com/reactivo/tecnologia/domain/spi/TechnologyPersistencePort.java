package com.reactivo.tecnologia.domain.spi;

import com.reactivo.tecnologia.domain.model.Technology;
import reactor.core.publisher.Mono;

public interface TechnologyPersistencePort {
    Mono<Technology> save(Technology technology);

    Mono<Boolean> existByName(String name);
}
