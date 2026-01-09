package com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.mapper;

import com.reactivo.tecnologia.domain.model.TechnologySummary;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.entity.TechnologySummaryDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TechnologySummaryEntityMapper {
    TechnologySummary toModel(TechnologySummaryDTO technologySummaryDTO);

}
