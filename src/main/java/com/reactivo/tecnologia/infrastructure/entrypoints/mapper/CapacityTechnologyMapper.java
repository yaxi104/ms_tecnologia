package com.reactivo.tecnologia.infrastructure.entrypoints.mapper;

import com.reactivo.tecnologia.domain.model.CapacityTechnology;
import com.reactivo.tecnologia.infrastructure.entrypoints.dto.request.CapacityTechnologyDTO;
import org.springframework.stereotype.Component;

@Component
public class CapacityTechnologyMapper {

    public CapacityTechnology toModel(CapacityTechnologyDTO dto) {
        if (dto == null) return null;

        return new CapacityTechnology(
                null,
                dto.idTechnology(),
                dto.idCapacity()
        );
    }
}
