package com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology.repository;

import com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology.entity.CapacityTechnologyEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CapacityTechnologyRepository extends ReactiveCrudRepository<CapacityTechnologyEntity, Long> {

    Flux<Long> findIdTechnologyByIdCapacity(Long idCapacity);

}
