package com.reactivo.tecnologia.infrastructure.entrypoints.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class TechnologyDTO {
    private String name;
    private String description;
}
