package com.reactivo.tecnologia.domain.usecase;

import com.reactivo.tecnologia.domain.api.CapacityTechnologyServicePort;
import com.reactivo.tecnologia.domain.enums.TechnicalMessage;
import com.reactivo.tecnologia.domain.exceptions.BusinessException;
import com.reactivo.tecnologia.domain.model.CapacityTechnology;
import com.reactivo.tecnologia.domain.model.CapacityTechnologyGroup;
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
import java.util.function.Function;
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

//    @Override
//    public Mono<Map<Long, List<TechnologySummary>>> findTechnologiesByCapacityIdsPaged(int page, int size, boolean asc) {
//        return capacityTechnologyPersistencePort.findByIdCapacityInPaged(page, size, asc)
//                .collectList()
//                .flatMap(capTechList -> {
//                    if (capTechList.isEmpty()) return Mono.just(Map.of());
//
//                    Map<Long, List<CapacityTechnology>> grouped = capTechList.stream()
//                            .collect(Collectors.groupingBy(CapacityTechnology::idCapacity));
//
//                    List<Long> sortedCapacityIds = grouped.entrySet().stream()
//                            .sorted((e1, e2) -> {
//                                int cmp = Integer.compare(e1.getValue().size(), e2.getValue().size());
//                                return asc ? cmp : -cmp;
//                            })
//                            .map(Map.Entry::getKey)
//                            .toList();
//
//                    List<Long> technologyIds = capTechList.stream()
//                            .map(CapacityTechnology::idTechnology)
//                            .filter(Objects::nonNull)
//                            .distinct()
//                            .toList();
//
//                    if (technologyIds.isEmpty()) {
//                        return Mono.just(
//                                sortedCapacityIds.stream()
//                                        .collect(Collectors.toMap(id -> id, id -> Collections.<TechnologySummary>emptyList()))
//                        );
//                    }
//
//                    return technologyPersistencePort.findByIds(technologyIds)
//                            .collectList()
//                            .map(techList -> mapCapacityTechnologies(capTechList, techList, sortedCapacityIds));
//                });
//    }

    @Override
    public Mono<Map<Long, List<TechnologySummary>>> findTechnologiesByCapacityIdsPaged(int page, int size, boolean asc) {

        return capacityTechnologyPersistencePort.findPaged(page, size, asc)
                .collectList()
                .flatMap(capGroups -> {
                    if (capGroups.isEmpty()) return Mono.just(Map.of());

                    List<Long> allTechIds = capGroups.stream()
                            .flatMap(g -> g.getIdTechnologies().stream())
                            .distinct()
                            .toList();

                    if (allTechIds.isEmpty()) return Mono.just(Map.of());

                    return technologyPersistencePort.findByIds(allTechIds)
                            .collectList()
                            .map(techList -> {
                                Map<Long, TechnologySummary> techById = techList.stream()
                                        .collect(Collectors.toMap(TechnologySummary::id, Function.identity()));

                                Map<Long, List<TechnologySummary>> result = new HashMap<>();
                                for (CapacityTechnologyGroup group : capGroups) {
                                    List<TechnologySummary> techsForCapacity = group.getIdTechnologies().stream()
                                            .map(techById::get)
                                            .filter(Objects::nonNull)
                                            .toList();
                                    result.put(group.getIdCapacity(), techsForCapacity);
                                }
                                return result;
                            });
                });
    }

//    private Map<Long, List<TechnologySummary>> mapCapacityTechnologies(List<CapacityTechnology> capTechList,
//                                                                       List<TechnologySummary> techList,
//                                                                       List<Long> sortedCapacityIds) {
//        Map<Long, TechnologySummary> techMap = techList.stream()
//                .collect(Collectors.toMap(TechnologySummary::id, t -> t));
//
//        Map<Long, List<TechnologySummary>> result = capTechList.stream()
//                .collect(Collectors.groupingBy(
//                        CapacityTechnology::idCapacity,
//                        Collectors.mapping(
//                                ct -> techMap.get(ct.idTechnology()),
//                                Collectors.toList()
//                        )
//                ));
//
//        LinkedHashMap<Long, List<TechnologySummary>> ordered = new LinkedHashMap<>();
//        for (Long id : sortedCapacityIds) {
//            ordered.put(id, result.getOrDefault(id, Collections.emptyList()));
//        }
//
//        return ordered;
//    }
}
