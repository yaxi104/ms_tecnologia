package com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology;

import com.reactivo.tecnologia.domain.model.CapacityTechnology;
import com.reactivo.tecnologia.domain.spi.CapacityTechnologyPersistencePort;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology.mapper.CapacityTechnologyEntityMapper;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology.repository.CapacityTechnologyRepository;
import lombok.AllArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;

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
                            SELECT id_technology, id_capacity
                            FROM TECHNOLOGY.CAPACITY_TECHNOLOGY
                            WHERE id_capacity IN (:ids)
                        """)
                .bind("ids", capacityIds)
                .map((row, meta) -> new CapacityTechnology(
                        null,
                        row.get("id_technology", Long.class),
                        row.get("id_capacity", Long.class)
                ))
                .all();
    }
}
