package com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.repository;

import com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.entity.TechnologyEntity;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.entity.TechnologySummaryDTO;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface TechnologyRepository extends ReactiveCrudRepository<TechnologyEntity, Long> {
    Mono<Boolean> existsByName(String name);

    @Query("SELECT id, name FROM TECHNOLOGY WHERE id IN (:ids)")
    Flux<TechnologySummaryDTO> findSummariesByIds(List<Long> ids);
}
