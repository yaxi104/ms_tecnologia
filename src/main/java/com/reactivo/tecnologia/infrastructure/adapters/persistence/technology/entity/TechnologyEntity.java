package com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "TECHNOLOGY")
public class TechnologyEntity {
    @Id
    private Long id;
    private String name;
    private String description;

    public TechnologyEntity() {
    }

    public TechnologyEntity(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
