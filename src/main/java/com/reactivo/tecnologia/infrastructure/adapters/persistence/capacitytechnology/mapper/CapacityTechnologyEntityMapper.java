package com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology.mapper;

import com.reactivo.tecnologia.domain.model.CapacityTechnology;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology.entity.CapacityTechnologyEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CapacityTechnologyEntityMapper {
    CapacityTechnology toModel(CapacityTechnologyEntity entity);

    CapacityTechnologyEntity toEntity(CapacityTechnology capacityTechnology);
}
