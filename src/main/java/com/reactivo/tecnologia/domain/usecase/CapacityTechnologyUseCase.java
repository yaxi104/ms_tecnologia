package com.reactivo.tecnologia.domain.usecase;

import com.reactivo.tecnologia.domain.api.CapacityTechnologyServicePort;
import com.reactivo.tecnologia.domain.model.CapacityTechnology;
import com.reactivo.tecnologia.domain.model.TechnologySummary;
import com.reactivo.tecnologia.domain.spi.CapacityTechnologyPersistencePort;
import com.reactivo.tecnologia.domain.spi.TechnologyPersistencePort;
import reactor.core.publisher.Flux;

public class CapacityTechnologyUseCase implements CapacityTechnologyServicePort {

    private final CapacityTechnologyPersistencePort capacityTechnologyPersistencePort;
    private final TechnologyPersistencePort technologyPersistencePort;

    public CapacityTechnologyUseCase(CapacityTechnologyPersistencePort capacityTechnologyPersistencePort, TechnologyPersistencePort technologyPersistencePort) {
        this.capacityTechnologyPersistencePort = capacityTechnologyPersistencePort;
        this.technologyPersistencePort = technologyPersistencePort;
    }

    @Override
    public Flux<CapacityTechnology> saveAllCapacityTechnology(Flux<CapacityTechnology> capacityTechnologies) {
        return capacityTechnologyPersistencePort.saveAll(capacityTechnologies);
    }

    @Override
    public Flux<TechnologySummary> findAllIdTechnologyByIdCapacity(Long idCapacity) {
        Flux<Long> technologyIds = capacityTechnologyPersistencePort.findAllIdTechnologyByIdCapacity(idCapacity);
        return technologyIds
                .collectList()
                .filter(ids -> !ids.isEmpty())
                .flatMapMany(technologyPersistencePort::findByIds);
    }
}
