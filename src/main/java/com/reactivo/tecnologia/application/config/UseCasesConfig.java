package com.reactivo.tecnologia.application.config;

import com.reactivo.tecnologia.domain.api.TechnologyServicePort;
import com.reactivo.tecnologia.domain.spi.TechnologyPersistencePort;
import com.reactivo.tecnologia.domain.usecase.TechnologyUseCase;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.TechnologyPersistenceAdapter;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.mapper.TechnologyEntityMapper;
import com.reactivo.tecnologia.infrastructure.adapters.persistence.technology.repository.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {
    private final TechnologyRepository technologyRepository;
    private final TechnologyEntityMapper technologyEntityMapper;

    @Bean
    public TechnologyPersistencePort technologyPersistencePort() {
        return new TechnologyPersistenceAdapter(technologyRepository, technologyEntityMapper);
    }

    @Bean
    public TechnologyServicePort technologyServicePort(TechnologyPersistencePort usersPersistencePort) {
        return new TechnologyUseCase(usersPersistencePort);
    }

}
