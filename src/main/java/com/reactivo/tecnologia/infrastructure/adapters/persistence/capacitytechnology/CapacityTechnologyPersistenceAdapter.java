package com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology;

import com.reactivo.tecnologia.domain.model.CapacityTechnology;
import com.reactivo.tecnologia.domain.model.CapacityTechnologyGroup;
import com.reactivo.tecnologia.domain.spi.CapacityTechnologyPersistencePort;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology.mapper.CapacityTechnologyEntityMapper;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology.repository.CapacityTechnologyRepository;
import lombok.AllArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

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
                            SELECT id_capacity, id_technology
                            FROM capacity_technology
                            WHERE id_capacity IN (:ids)
                        """)
                .bind("ids", capacityIds)
                .map((row, meta) -> new CapacityTechnology(
                        null,
                        row.get("id_capacity", Long.class),
                        row.get("id_technology", Long.class)
                ))
                .all();
    }

    @Override
    public Flux<CapacityTechnology> findByIdCapacityInPaged(int page, int size, boolean asc) {
        int offset = page * size;

        String sql = """
                SELECT id_capacity, id_technology
                FROM capacity_technology
                ORDER BY id_capacity ASC
                LIMIT :limit OFFSET :offset
                """;

        return databaseClient.sql(sql)
                .bind("limit", size)
                .bind("offset", offset)
                .map((row, meta) -> new CapacityTechnology(
                        null,
                        row.get("id_capacity", Long.class),
                        row.get("id_technology", Long.class)
                ))
                .all();
    }

//    public Flux<CapacityTechnologyGroup> findGroupedOrderedAndPaged(
//            List<Long> idCapacities,
//            int page,
//            int size,
//            Sort.Direction direction
//    ) {
//
//        return capacityTechnologyRepository.findByIdCapacityIn(idCapacities)
//
//                .groupBy(CapacityTechnologyEntity::getIdCapacity)
//
//                .flatMap(group ->
//                        group.map(CapacityTechnologyEntity::getIdTechnology)
//                                .distinct()
//                                .collectList()
//                                .map(techs ->
//                                        new CapacityTechnologyGroup(group.key(), techs)
//                                )
//                )
//
//                .sort((a, b) ->
//                        direction.isAscending()
//                                ? Integer.compare(a.getTechnologyCount(), b.getTechnologyCount())
//                                : Integer.compare(b.getTechnologyCount(), a.getTechnologyCount())
//                )
//
//                .skip((long) page * size)
//                .take(size);
//    }

    @Override
    public Flux<CapacityTechnologyGroup> findPaged(int page, int size, boolean asc) {
        int offset = page * size;
        String order = asc ? "ASC" : "DESC";

        String sql = """
                SELECT
                    id_capacity AS idCapacity,
                    COUNT(DISTINCT id_technology) AS technologyCount,
                    GROUP_CONCAT(DISTINCT id_technology ORDER BY id_technology SEPARATOR ',') AS idTechnologies
                FROM capacity_technology
                GROUP BY id_capacity
                ORDER BY technologyCount %s
                LIMIT :limit OFFSET :offset
                """.formatted(order);

        return databaseClient.sql(sql)
                .bind("limit", size)
                .bind("offset", offset)
                .map((row, meta) -> {
                    String techsCsv = row.get("idTechnologies", String.class);
                    List<Long> techs = techsCsv == null || techsCsv.isEmpty()
                            ? List.of()
                            : Arrays.stream(techsCsv.split(","))
                            .map(Long::parseLong)
                            .toList();

                    return new CapacityTechnologyGroup(
                            row.get("idCapacity", Long.class),
                            techs
                    );
                })
                .all();
    }
}
