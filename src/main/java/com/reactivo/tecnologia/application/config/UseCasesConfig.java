package com.reactivo.tecnologia.application.config;

import com.reactivo.tecnologia.domain.api.CapacityTechnologyServicePort;
import com.reactivo.tecnologia.domain.api.TechnologyServicePort;
import com.reactivo.tecnologia.domain.spi.CapacityTechnologyPersistencePort;
import com.reactivo.tecnologia.domain.spi.TechnologyPersistencePort;
import com.reactivo.tecnologia.domain.usecase.CapacityTechnologyUseCase;
import com.reactivo.tecnologia.domain.usecase.TechnologyUseCase;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology.CapacityTechnologyPersistenceAdapter;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology.mapper.CapacityTechnologyEntityMapper;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.capacitytechnology.repository.CapacityTechnologyRepository;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.TechnologyPersistenceAdapter;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.mapper.TechnologyEntityMapper;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.mapper.TechnologySummaryEntityMapper;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.repository.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {
    private final TechnologyRepository technologyRepository;
    private final TechnologyEntityMapper technologyEntityMapper;
    private final TechnologySummaryEntityMapper technologySummaryEntityMapper;
    private final CapacityTechnologyRepository capacityTechnologyRepository;
    private final CapacityTechnologyEntityMapper capacityTechnologyEntityMapper;
    private final TransactionalOperator transactionalOperator;
    private final DatabaseClient databaseClient;

    @Bean
    public TechnologyPersistencePort technologyPersistencePort() {
        return new TechnologyPersistenceAdapter(technologyRepository, technologyEntityMapper, technologySummaryEntityMapper, databaseClient);
    }

    @Bean
    public CapacityTechnologyPersistencePort capacityTechnologyPersistencePort() {
        return new CapacityTechnologyPersistenceAdapter(capacityTechnologyRepository, capacityTechnologyEntityMapper, transactionalOperator, databaseClient);
    }

    @Bean
    public TechnologyServicePort technologyServicePort(TechnologyPersistencePort usersPersistencePort) {
        return new TechnologyUseCase(usersPersistencePort);
    }

    @Bean
    public CapacityTechnologyServicePort capacityTechnologyServicePort(CapacityTechnologyPersistencePort capacityTechnologyPersistencePort, TechnologyPersistencePort usersPersistencePort) {
        return new CapacityTechnologyUseCase(capacityTechnologyPersistencePort, usersPersistencePort);
    }
}
