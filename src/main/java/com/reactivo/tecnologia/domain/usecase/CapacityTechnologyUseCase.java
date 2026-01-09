package com.reactivo.tecnologia.domain.usecase;

import com.reactivo.tecnologia.domain.api.CapacityTechnologyServicePort;
import com.reactivo.tecnologia.domain.enums.TechnicalMessage;
import com.reactivo.tecnologia.domain.exceptions.BusinessException;
import com.reactivo.tecnologia.domain.model.CapacityTechnology;
import com.reactivo.tecnologia.domain.model.TechnologySummary;
import com.reactivo.tecnologia.domain.spi.CapacityTechnologyPersistencePort;
import com.reactivo.tecnologia.domain.spi.TechnologyPersistencePort;
import reactor.core.publisher.Flux;

import java.util.Objects;

public class CapacityTechnologyUseCase implements CapacityTechnologyServicePort {

    private final CapacityTechnologyPersistencePort persistencePort;
    private final TechnologyPersistencePort technologyPersistencePort;

    public CapacityTechnologyUseCase(CapacityTechnologyPersistencePort persistencePort, TechnologyPersistencePort technologyPersistencePort) {
        this.persistencePort = persistencePort;
        this.technologyPersistencePort = technologyPersistencePort;
    }

    @Override
    public Flux<CapacityTechnology> saveAllCapacityTechnology(
            Flux<CapacityTechnology> capacityTechnologies) {

        return capacityTechnologies
                .filter(Objects::nonNull)
                .collectList()
                .flatMapMany(list -> {
                    if (list.isEmpty()) {
                        return Flux.error(
                                new BusinessException(TechnicalMessage.INVALID_PARAMETERS)
                        );
                    }
                    return persistencePort.saveAll(Flux.fromIterable(list));
                });
    }

    @Override
    public Flux<TechnologySummary> findAllIdTechnologyByIdCapacity(Long idCapacity) {
        return persistencePort.findAllIdTechnologyByIdCapacity(idCapacity)
                .collectList()
                .filter(ids -> !ids.isEmpty())
                .flatMapMany(technologyPersistencePort::findByIds);
    }
}
