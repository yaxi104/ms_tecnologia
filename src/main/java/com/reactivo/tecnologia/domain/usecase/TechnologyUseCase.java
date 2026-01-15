package com.reactivo.tecnologia.domain.usecase;

import com.reactivo.tecnologia.domain.api.TechnologyServicePort;
import com.reactivo.tecnologia.domain.enums.TechnicalMessage;
import com.reactivo.tecnologia.domain.exceptions.BusinessException;
import com.reactivo.tecnologia.domain.model.Technology;
import com.reactivo.tecnologia.domain.model.TechnologySummary;
import com.reactivo.tecnologia.domain.spi.TechnologyPersistencePort;
import com.reactivo.tecnologia.domain.utils.ValidationHelper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class TechnologyUseCase implements TechnologyServicePort {

    private final TechnologyPersistencePort technologyPersistencePort;

    public TechnologyUseCase(TechnologyPersistencePort technologyPersistencePort) {
        this.technologyPersistencePort = technologyPersistencePort;
    }

    @Override
    public Mono<Technology> saveTechnology(Technology technology) {
        return Mono.when(
                        ValidationHelper.validateRequest(technology, t -> t.name() != null && !t.name().isBlank() && t.name().length() <= 50),
                        ValidationHelper.validateRequest(technology, t -> t.description() != null && !t.description().isBlank() && t.description().length() <= 90)
                ).then(technologyPersistencePort.existByName(technology.name()))
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.error(new BusinessException(
                        TechnicalMessage.TECHNOLOGY_ALREADY_EXISTS))
                        : technologyPersistencePort.save(technology)
                );
    }

    @Override
    public Flux<Technology> findAll() {
        return technologyPersistencePort.findAll();
    }

    @Override
    public Flux<TechnologySummary> findByIds(List<Long> ids) {
        return technologyPersistencePort.findByIds(ids);
    }

    @Override
    public Mono<Void> deleteTechnologiesByCapacities(List<Long> capacityIds) {
        Mono<Void> deleteRelations = technologyPersistencePort.deleteCapacityTechnologyRelations(capacityIds);
        Mono<Void> deleteOrphanTechnologies = technologyPersistencePort.findOrphanedTechnologies(capacityIds)
                .flatMap(technologyPersistencePort::deleteTechnology)
                .then();

        return deleteRelations.then(deleteOrphanTechnologies);
    }
}
