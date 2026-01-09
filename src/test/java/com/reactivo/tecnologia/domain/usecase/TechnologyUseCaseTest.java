package com.reactivo.tecnologia.domain.usecase;

import com.reactivo.tecnologia.domain.enums.TechnicalMessage;
import com.reactivo.tecnologia.domain.exceptions.BusinessException;
import com.reactivo.tecnologia.domain.model.Technology;
import com.reactivo.tecnologia.domain.spi.TechnologyPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TechnologyUseCaseTest {

    @Mock
    private TechnologyPersistencePort technologyPersistencePort;

    @InjectMocks
    private TechnologyUseCase technologyUseCase;

    private Technology validTechnology;

    @BeforeEach
    void setUp() {
        validTechnology = new Technology(
                null,
                "Spring WebFlux",
                "Reactive framework"
        );
    }

    @Test
    void saveTechnologySuccessTest() {

        when(technologyPersistencePort.existByName(validTechnology.name()))
                .thenReturn(Mono.just(false));

        when(technologyPersistencePort.save(validTechnology))
                .thenReturn(Mono.just(validTechnology));

        StepVerifier.create(technologyUseCase.saveTechnology(validTechnology))
                .expectNext(validTechnology)
                .verifyComplete();

        verify(technologyPersistencePort).existByName(validTechnology.name());
        verify(technologyPersistencePort).save(validTechnology);
    }

    @Test
    void saveTechnologyExistsTest() {

        when(technologyPersistencePort.existByName(validTechnology.name()))
                .thenReturn(Mono.just(true));

        StepVerifier.create(technologyUseCase.saveTechnology(validTechnology))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                ((BusinessException) error)
                                        .getTechnicalMessage()
                                        .equals(TechnicalMessage.TECHNOLOGY_ALREADY_EXISTS)
                )
                .verify();

        verify(technologyPersistencePort).existByName(validTechnology.name());
        verify(technologyPersistencePort, never()).save(any());
    }


    @Test
    void saveTechnologyNameIsInvalidTest() {
        Technology invalidName = new Technology(null, "", "Valid description");

        when(technologyPersistencePort.existByName(anyString())).thenReturn(Mono.just(false));

        StepVerifier.create(technologyUseCase.saveTechnology(invalidName))
                .expectErrorMatches(err ->
                        err instanceof BusinessException &&
                                ((BusinessException) err).getTechnicalMessage() == TechnicalMessage.INVALID_REQUEST
                )
                .verify();

        verify(technologyPersistencePort, never()).save(any());
    }

    @Test
    void saveTechnologyDescriptionIsInvalidTest() {
        Technology invalidDescription = new Technology(null, "Valid Name", "");

        when(technologyPersistencePort.existByName(anyString())).thenReturn(Mono.just(false));

        StepVerifier.create(technologyUseCase.saveTechnology(invalidDescription))
                .expectErrorMatches(err ->
                        err instanceof BusinessException &&
                                ((BusinessException) err).getTechnicalMessage() == TechnicalMessage.INVALID_REQUEST
                )
                .verify();

        verify(technologyPersistencePort, never()).save(any());
    }
}