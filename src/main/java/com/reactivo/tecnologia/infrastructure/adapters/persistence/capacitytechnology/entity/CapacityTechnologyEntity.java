package com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "CAPACITY_TECHNOLOGY")
public class CapacityTechnologyEntity {
    @Id
    private Long id;
    @JsonProperty("id_technology")
    private Long idTechnology;
    @JsonProperty("id_capacity")
    private Long idCapacity;

    public CapacityTechnologyEntity() {
    }

    public CapacityTechnologyEntity(Long id, Long idTechnology, Long idCapacity) {
        this.id = id;
        this.idTechnology = idTechnology;
        this.idCapacity = idCapacity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdTechnology() {
        return idTechnology;
    }

    public void setIdTechnology(Long idTechnology) {
        this.idTechnology = idTechnology;
    }

    public Long getIdCapacity() {
        return idCapacity;
    }

    public void setIdCapacity(Long idCapacity) {
        this.idCapacity = idCapacity;
    }
}
