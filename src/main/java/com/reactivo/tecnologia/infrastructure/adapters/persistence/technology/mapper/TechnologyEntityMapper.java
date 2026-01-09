package com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.mapper;

import com.reactivo.tecnologia.domain.model.Technology;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.entity.TechnologyEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TechnologyEntityMapper {
    Technology toModel(TechnologyEntity entity);

    TechnologyEntity toEntity(Technology technology);
}
