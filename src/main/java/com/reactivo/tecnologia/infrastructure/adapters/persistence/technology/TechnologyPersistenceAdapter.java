package com.reactivo.tecnologia.infrastructure.adapters.persistence.technology;

import com.reactivo.tecnologia.domain.model.Technology;
import com.reactivo.tecnologia.domain.spi.TechnologyPersistencePort;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.mapper.TechnologyEntityMapper;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.repository.TechnologyRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class TechnologyPersistenceAdapter implements TechnologyPersistencePort {
    private final TechnologyRepository technologyRepository;
    private final TechnologyEntityMapper technologyEntityMapper;

    @Override
    public Mono<Technology> save(Technology technology) {
        return technologyRepository.save(technologyEntityMapper.toEntity(technology)).map(technologyEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> existByName(String name) {
        return technologyRepository.existsByName(name);
    }

}
