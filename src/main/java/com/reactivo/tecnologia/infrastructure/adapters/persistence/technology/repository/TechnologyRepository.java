package com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.repository;

import com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.entity.TechnologyEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TechnologyRepository extends ReactiveCrudRepository<TechnologyEntity, Long> {
    Mono<Boolean> existsByName(String name);
}
