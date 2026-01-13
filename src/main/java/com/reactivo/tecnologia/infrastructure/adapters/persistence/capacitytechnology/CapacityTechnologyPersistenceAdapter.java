package com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology;

import com.reactivo.tecnologia.domain.model.CapacityTechnology;
import com.reactivo.tecnologia.domain.model.TechnologySummary;
import com.reactivo.tecnologia.domain.spi.CapacityTechnologyPersistencePort;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology.mapper.CapacityTechnologyEntityMapper;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology.repository.CapacityTechnologyRepository;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology.util.DatabaseConstants;
import lombok.AllArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CapacityTechnologyPersistenceAdapter implements CapacityTechnologyPersistencePort {

    private final CapacityTechnologyRepository capacityTechnologyRepository;
    private final CapacityTechnologyEntityMapper capacityTechnologyEntityMapper;
    private final TransactionalOperator transactionalOperator;
    private final DatabaseClient databaseClient;

    @Override
    public Flux<CapacityTechnology> saveAll(Flux<CapacityTechnology> technologies) {
        return transactionalOperator.execute(status ->
                technologies
                        .map(capacityTechnologyEntityMapper::toEntity)
                        .as(capacityTechnologyRepository::saveAll)
        ).map(capacityTechnologyEntityMapper::toModel);
    }

    @Override
    public Flux<Long> findAllIdTechnologyByIdCapacity(Long idCapacity) {
        return capacityTechnologyRepository.findIdTechnologyByIdCapacity(idCapacity);
    }

    @Override
    public Flux<CapacityTechnology> findByIdCapacityIn(List<Long> capacityIds) {
        if (capacityIds.isEmpty()) return Flux.empty();

        return databaseClient.sql("""
                        SELECT %s, %s
                        FROM TECHNOLOGY.CAPACITY_TECHNOLOGY
                        WHERE %s IN (:ids)
                        """.formatted(
                        DatabaseConstants.COLUMN_ID_TECHNOLOGY,
                        DatabaseConstants.COLUMN_ID_CAPACITY,
                        DatabaseConstants.COLUMN_ID_CAPACITY
                ))
                .bind("ids", capacityIds)
                .map((row, meta) -> new CapacityTechnology(
                        null,
                        row.get(DatabaseConstants.COLUMN_ID_TECHNOLOGY, Long.class),
                        row.get(DatabaseConstants.COLUMN_ID_CAPACITY, Long.class)
                ))
                .all();
    }

    @Override
    public Mono<Map<Long, List<TechnologySummary>>> getCapacityIdGroupedTechnologiesAsMap(int page, int size, boolean asc) {
        String order = asc ? "ASC" : "DESC";

        Mono<List<Long>> pagedCapacityIds = databaseClient.sql(
                        DatabaseConstants.PAGED_CAPACITY_IDS_QUERY.formatted(order))
                .bind("limit", size)
                .bind("offset", page * size)
                .map(row -> row.get(DatabaseConstants.COLUMN_ID_CAPACITY, Long.class))
                .all()
                .collectList();

        return pagedCapacityIds.flatMap(this::fetchTechnologiesForCapacityIds);
    }

    private Mono<Map<Long, List<TechnologySummary>>> fetchTechnologiesForCapacityIds(List<Long> capacityIds) {
        if (capacityIds.isEmpty()) return Mono.just(Collections.emptyMap());

        return databaseClient.sql(
                        DatabaseConstants.FETCH_TECHNOLOGIES_QUERY.formatted(
                                DatabaseConstants.COLUMN_ID_CAPACITY,
                                DatabaseConstants.COLUMN_ID_TECH,
                                DatabaseConstants.COLUMN_TECH_NAME,
                                DatabaseConstants.COLUMN_ID_CAPACITY,
                                DatabaseConstants.COLUMN_ID_CAPACITY
                        ))
                .bind("capacityIds", capacityIds)
                .map((row, meta) -> Map.entry(
                        Objects.requireNonNull(row.get(DatabaseConstants.COLUMN_ID_CAPACITY, Long.class)),
                        new TechnologySummary(
                                row.get(DatabaseConstants.COLUMN_ID_TECH, Long.class),
                                row.get(DatabaseConstants.COLUMN_TECH_NAME, String.class)
                        )
                ))
                .all()
                .collectMultimap(Map.Entry::getKey, Map.Entry::getValue)
                .map(multimap -> multimap.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> {
                                    List<TechnologySummary> list = new ArrayList<>(e.getValue());
                                    list.sort(Comparator.comparing(TechnologySummary::id));
                                    return list;
                                }
                        ))
                );
    }
}
