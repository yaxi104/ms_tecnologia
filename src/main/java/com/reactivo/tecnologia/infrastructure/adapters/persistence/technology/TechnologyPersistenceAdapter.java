package com.reactivo.tecnologia.infrastructure.adapters.persistence.technology;

import com.reactivo.tecnologia.domain.model.Technology;
import com.reactivo.tecnologia.domain.model.TechnologySummary;
import com.reactivo.tecnologia.domain.spi.TechnologyPersistencePort;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.mapper.TechnologyEntityMapper;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.mapper.TechnologySummaryEntityMapper;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.repository.TechnologyRepository;
import lombok.AllArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
public class TechnologyPersistenceAdapter implements TechnologyPersistencePort {
    private final TechnologyRepository technologyRepository;
    private final TechnologyEntityMapper technologyEntityMapper;
    private final TechnologySummaryEntityMapper technologySummaryEntityMapper;
    private final DatabaseClient databaseClient;

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

    @Override
    public Mono<Technology> findById(Long id) {
        return technologyRepository.findById(id)
                .map(technologyEntityMapper::toModel);
    }

    @Override
    public Flux<Long> findOrphanedTechnologies(List<Long> capacityIds) {
        return databaseClient.sql("""
                        SELECT t.id
                        FROM technology t
                        LEFT JOIN capacity_technology ct ON t.id = ct.id_technology
                        GROUP BY t.id
                        HAVING COUNT(ct.id_capacity) = 0
                        """)
                .map(row -> row.get("id", Long.class))
                .all();
    }

    @Override
    @Transactional
    public Mono<Void> deleteTechnology(Long technologyId) {
        return technologyRepository.deleteById(technologyId);
    }

    @Override
    @Transactional
    public Mono<Void> deleteCapacityTechnologyRelations(List<Long> capacityIds) {
        return databaseClient.sql("DELETE FROM capacity_technology WHERE id_capacity IN (:ids)")
                .bind("ids", capacityIds)
                .then();
    }

}
