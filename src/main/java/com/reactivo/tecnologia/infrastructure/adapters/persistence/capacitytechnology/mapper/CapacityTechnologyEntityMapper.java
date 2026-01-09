package com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology.mapper;

import com.reactivo.tecnologia.domain.model.CapacityTechnology;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology.entity.CapacityTechnologyEntity;
import org.mapstruct.Mapper;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface CapacityTechnologyEntityMapper {

    default CapacityTechnology toModel(CapacityTechnologyEntity entity) {
        Objects.requireNonNull(entity, "CapacityTechnologyEntity no puede ser null");
        return new CapacityTechnology(
                entity.getId(),
                entity.getIdTechnology(),
                entity.getIdCapacity()
        );
    }

    default CapacityTechnologyEntity toEntity(CapacityTechnology capacityTechnology) {
        Objects.requireNonNull(capacityTechnology, "CapacityTechnology no puede ser null");
        return new CapacityTechnologyEntity(
                capacityTechnology.id(),
                capacityTechnology.idTechnology(),
                capacityTechnology.idCapacity()
        );
    }
}
