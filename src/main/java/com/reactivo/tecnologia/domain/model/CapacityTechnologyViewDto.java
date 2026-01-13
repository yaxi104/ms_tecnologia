package com.reactivo.tecnologia.domain.model;

public class CapacityTechnologyViewDto
        implements CapacityTechnologyView {

    private final Long idCapacity;
    private final Integer technologyCount;

    public CapacityTechnologyViewDto(Long idCapacity, Integer technologyCount) {
        this.idCapacity = idCapacity;
        this.technologyCount = technologyCount;
    }

    @Override
    public Long getIdCapacity() {
        return idCapacity;
    }

    @Override
    public Integer getTechnologyCount() {
        return technologyCount;
    }
}
