package com.reactivo.tecnologia.infrastructure.entrypoints.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class TechnologyResponse {
    private Long id;
    private String name;
    private String description;
}
