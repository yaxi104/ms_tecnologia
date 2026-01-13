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
import java.util.HashMap;
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
        if (capacityIds == null || capacityIds.isEmpty()) {
            return Mono.just(Map.of());
        }
        return capacityTechnologyPersistencePort.findByIdCapacityIn(capacityIds)
                .collectList()
                .flatMap(capacityTechnologies -> {
                    if (capacityTechnologies.isEmpty()) {
                        return Mono.just(Map.of());
                    }

                    // Paso 2: Agrupar por idCapacity y obtener la lista de idTechnology de cada capacidad
                    Map<Long, List<Long>> capacityToTechIds = capacityTechnologies.stream()
                            .collect(Collectors.groupingBy(
                                    CapacityTechnology::idCapacity,
                                    Collectors.mapping(CapacityTechnology::idTechnology, Collectors.toList())
                            ));

                    // Paso 3: obtener todos los idTechnology distintos
                    List<Long> allTechnologyIds = capacityToTechIds.values().stream()
                            .flatMap(List::stream)
                            .distinct()
                            .toList();

                    if (allTechnologyIds.isEmpty()) {
                        return Mono.just(Map.of());
                    }

                    // Paso 4: traer los TechnologySummary de cada idTechnology
                    return technologyPersistencePort.findByIds(allTechnologyIds)
                            .collectList()
                            .map(allTechSummaries -> {
                                // Paso 5: Construir el mapa final capacityId -> List<TechnologySummary>
                                Map<Long, List<TechnologySummary>> result = new HashMap<>();
                                for (Map.Entry<Long, List<Long>> entry : capacityToTechIds.entrySet()) {
                                    Long capacityId = entry.getKey();
                                    List<Long> techIds = entry.getValue();

                                    List<TechnologySummary> techSummaries = allTechSummaries.stream()
                                            .filter(ts -> techIds.contains(ts.id()))
                                            .collect(Collectors.toList());

                                    result.put(capacityId, techSummaries);
                                }
                                return result;
                            });
                });
    }
}
