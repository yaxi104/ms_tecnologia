package com.reactivo.tecnologia.infrastructure.entrypoints.mapper;

import com.reactivo.tecnologia.domain.model.Technology;
import com.reactivo.tecnologia.infrastructure.entrypoints.dto.request.TechnologyDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TechnologyMapper {

    Technology technologyDTOToTechnology(TechnologyDTO technologyDTO);
}
