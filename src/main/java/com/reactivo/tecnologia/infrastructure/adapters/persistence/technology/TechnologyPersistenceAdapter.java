package com.reactivo.tecnologia.infrastructure.adapters.persistence.technology;

import com.reactivo.tecnologia.domain.model.Technology;
import com.reactivo.tecnologia.domain.model.TechnologySummary;
import com.reactivo.tecnologia.domain.spi.TechnologyPersistencePort;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.mapper.TechnologyEntityMapper;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.mapper.TechnologySummaryEntityMapper;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.repository.TechnologyRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
public class TechnologyPersistenceAdapter implements TechnologyPersistencePort {
    private final TechnologyRepository technologyRepository;
    private final TechnologyEntityMapper technologyEntityMapper;
    private final TechnologySummaryEntityMapper technologySummaryEntityMapper;


    @Override
    public Mono<Technology> save(Technology technology) {
        return technologyRepository.save(technologyEntityMapper.toEntity(technology)).map(technologyEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> existByName(String name) {
        return technologyRepository.existsByName(name);
    }

    @Override
    public Flux<Technology> findAll() {
        return technologyRepository.findAll()
                .map(technologyEntityMapper::toModel);
    }

    @Override
    public Flux<TechnologySummary> findByIds(List<Long> ids) {
        if (ids.isEmpty()) return Flux.empty();
        return technologyRepository.findSummariesByIds(ids)
                .map(technologySummaryEntityMapper::toModel);
    }

}
