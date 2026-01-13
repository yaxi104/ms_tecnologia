package com.reactivo.tecnologia.domain.usecase;

import com.reactivo.tecnologia.domain.api.CapacityTechnologyServicePort;
import com.reactivo.tecnologia.domain.enums.TechnicalMessage;
import com.reactivo.tecnologia.domain.exceptions.BusinessException;
import com.reactivo.tecnologia.domain.model.CapacityTechnology;
import com.reactivo.tecnologia.domain.model.TechnologySummary;
import com.reactivo.tecnologia.domain.spi.CapacityTechnologyPersistencePort;
import com.reactivo.tecnologia.domain.spi.TechnologyPersistencePort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CapacityTechnologyUseCase implements CapacityTechnologyServicePort {

    private final CapacityTechnologyPersistencePort capacityTechnologyPersistencePort;
    private final TechnologyPersistencePort technologyPersistencePort;

    public CapacityTechnologyUseCase(CapacityTechnologyPersistencePort persistencePort, TechnologyPersistencePort technologyPersistencePort) {
        this.capacityTechnologyPersistencePort = persistencePort;
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
                    return capacityTechnologyPersistencePort.saveAll(Flux.fromIterable(list));
                });
    }

    @Override
    public Flux<TechnologySummary> findAllIdTechnologyByIdCapacity(Long idCapacity) {
        return capacityTechnologyPersistencePort.findAllIdTechnologyByIdCapacity(idCapacity)
                .collectList()
                .filter(ids -> !ids.isEmpty())
                .flatMapMany(technologyPersistencePort::findByIds);
    }

    @Override
    public Mono<Map<Long, List<TechnologySummary>>> findTechnologiesByCapacityIds(List<Long> capacityIds) {

        return capacityTechnologyPersistencePort.findByIdCapacityIn(capacityIds)
                .collectList()
                .flatMap(capacityTechnologies -> {

                    Map<Long, List<Long>> techToCapacities =
                            capacityTechnologies.stream()
                                    .collect(Collectors.groupingBy(
                                            CapacityTechnology::idTechnology,
                                            Collectors.mapping(
                                                    CapacityTechnology::idCapacity,
                                                    Collectors.toList()
                                            )
                                    ));

                    if (techToCapacities.isEmpty()) {
                        return Mono.just(Map.of());
                    }

                    List<Long> technologyIds = new ArrayList<>(techToCapacities.keySet());

                    return technologyPersistencePort.findByIds(technologyIds)
                            .flatMap(techSummary ->
                                    Flux.fromIterable(
                                            techToCapacities.get(techSummary.id())
                                    ).map(capacityId ->
                                            Map.entry(capacityId, techSummary)
                                    )
                            )
                            .collectMultimap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue
                            )
                            .map(map -> map.entrySet()
                                    .stream()
                                    .collect(Collectors.toMap(
                                            Map.Entry::getKey,
                                            e -> new ArrayList<>(e.getValue())
                                    )));
                });
    }
}
