package com.reactivo.tecnologia.domain.model;

import java.util.List;

public class CapacityTechnologyGroup {

    private Long idCapacity;
    private List<Long> idTechnologies;
    private int technologyCount;

    public CapacityTechnologyGroup(Long idCapacity, List<Long> idTechnologies) {
        this.idCapacity = idCapacity;
        this.idTechnologies = idTechnologies;
        this.technologyCount = idTechnologies.size();
    }

    public Long getIdCapacity() {
        return idCapacity;
    }

    public List<Long> getIdTechnologies() {
        return idTechnologies;
    }

    public int getTechnologyCount() {
        return technologyCount;
    }
}
