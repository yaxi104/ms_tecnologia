package com.reactivo.tecnologia.infrastructure.entrypoints.mapper;

import com.reactivo.tecnologia.domain.model.Technology;
import com.reactivo.tecnologia.infrastructure.entrypoints.dto.response.TechnologyResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TechnologyResponseMapper {
    TechnologyResponse toDto(Technology technology);
}
