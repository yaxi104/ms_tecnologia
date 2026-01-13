package com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology;

import com.reactivo.tecnologia.domain.model.CapacityTechnology;
import com.reactivo.tecnologia.domain.spi.CapacityTechnologyPersistencePort;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology.mapper.CapacityTechnologyEntityMapper;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology.repository.CapacityTechnologyRepository;
import lombok.AllArgsConstructor;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;

import java.util.List;

@AllArgsConstructor
public class CapacityTechnologyPersistenceAdapter implements CapacityTechnologyPersistencePort {

    private final CapacityTechnologyRepository capacityTechnologyRepository;
    private final CapacityTechnologyEntityMapper capacityTechnologyEntityMapper;
    private final TransactionalOperator transactionalOperator;

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
    public Flux<CapacityTechnology> findByIdCapacityIn(List<Long> idCapacities) {
        return capacityTechnologyRepository.findByIdCapacityIn(idCapacities)
                .map(capacityTechnologyEntityMapper::toModel);
    }

}
