package com.reactivo.tecnologia.infrastructure.entrypoints.mapper;

import com.reactivo.tecnologia.domain.model.TechnologySummary;
import com.reactivo.tecnologia.infrastructure.entrypoints.dto.response.TechnologySummaryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TechnologySummaryResponseMapper {
    TechnologySummaryResponse toDto(TechnologySummary technologySummary);
}
