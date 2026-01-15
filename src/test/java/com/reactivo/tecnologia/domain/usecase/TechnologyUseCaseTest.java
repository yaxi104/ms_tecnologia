package com.reactivo.tecnologia.domain.usecase;

import com.reactivo.tecnologia.domain.enums.TechnicalMessage;
import com.reactivo.tecnologia.domain.exceptions.BusinessException;
import com.reactivo.tecnologia.domain.model.Technology;
import com.reactivo.tecnologia.domain.model.TechnologySummary;
import com.reactivo.tecnologia.domain.spi.TechnologyPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
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


    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource( strings = {"", " ", "Este Es Un Nombre De Tecnologia Que Supera Cincuenta Caracteres."})
    void saveTechnologyNameIsInvalidTest(String arg) {
        Technology invalidName = new Technology(null, arg, "Valid description");

        lenient().when(technologyPersistencePort.existByName(any())).thenReturn(Mono.just(false));

        StepVerifier.create(technologyUseCase.saveTechnology(invalidName))
                .expectErrorMatches(err ->
                        err instanceof BusinessException &&
                                ((BusinessException) err).getTechnicalMessage() == TechnicalMessage.INVALID_REQUEST
                )
                .verify();

        verify(technologyPersistencePort, never()).save(any());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource( strings = {"", " ", "Esta es una descripción de prueba que supera los noventa caracteres para testear la validación de longitud."})
    void saveTechnologyDescriptionIsInvalidTest(String arg) {
        Technology invalidDescription = new Technology(null, "Valid Name", arg);

        when(technologyPersistencePort.existByName(anyString())).thenReturn(Mono.just(false));

        StepVerifier.create(technologyUseCase.saveTechnology(invalidDescription))
                .expectErrorMatches(err ->
                        err instanceof BusinessException &&
                                ((BusinessException) err).getTechnicalMessage() == TechnicalMessage.INVALID_REQUEST
                )
                .verify();

        verify(technologyPersistencePort, never()).save(any());
    }

    @Test
    void findAllReturnsTechnologiesTest() {
        Technology tech1 = new Technology(1L, "Spring", "Framework");
        Technology tech2 = new Technology(2L, "React", "Frontend");

        when(technologyPersistencePort.findAll())
                .thenReturn(Flux.just(tech1, tech2));

        StepVerifier.create(technologyUseCase.findAll())
                .expectNext(tech1)
                .expectNext(tech2)
                .verifyComplete();

        verify(technologyPersistencePort).findAll();
    }

    @Test
    void findAllEmptyTest() {
        when(technologyPersistencePort.findAll())
                .thenReturn(Flux.empty());

        StepVerifier.create(technologyUseCase.findAll())
                .verifyComplete();

        verify(technologyPersistencePort).findAll();
    }

    @Test
    void findByIdsReturnsSummariesTest() {
        TechnologySummary summary1 = new TechnologySummary(1L, "Spring");
        TechnologySummary summary2 = new TechnologySummary(2L, "React");

        List<Long> ids = List.of(1L, 2L);

        when(technologyPersistencePort.findByIds(ids))
                .thenReturn(Flux.just(summary1, summary2));

        StepVerifier.create(technologyUseCase.findByIds(ids))
                .expectNext(summary1)
                .expectNext(summary2)
                .verifyComplete();

        verify(technologyPersistencePort).findByIds(ids);
    }

    @Test
    void findByIdsEmptyTest() {
        List<Long> ids = List.of(99L);

        when(technologyPersistencePort.findByIds(ids))
                .thenReturn(Flux.empty());

        StepVerifier.create(technologyUseCase.findByIds(ids))
                .verifyComplete();

        verify(technologyPersistencePort).findByIds(ids);
    }

    @Test
    void deleteTechnologiesByCapacitiesSuccessTest() {
        List<Long> capacityIds = List.of(1L, 2L);

        when(technologyPersistencePort.deleteCapacityTechnologyRelations(capacityIds))
                .thenReturn(Mono.empty());

        when(technologyPersistencePort.findOrphanedTechnologies(capacityIds))
                .thenReturn(Flux.just(10L, 20L));

        when(technologyPersistencePort.deleteTechnology(10L))
                .thenReturn(Mono.empty());

        when(technologyPersistencePort.deleteTechnology(20L))
                .thenReturn(Mono.empty());

        StepVerifier.create(
                        technologyUseCase.deleteTechnologiesByCapacities(capacityIds)
                )
                .verifyComplete();

        verify(technologyPersistencePort)
                .deleteCapacityTechnologyRelations(capacityIds);
        verify(technologyPersistencePort)
                .findOrphanedTechnologies(capacityIds);
        verify(technologyPersistencePort)
                .deleteTechnology(10L);
        verify(technologyPersistencePort)
                .deleteTechnology(20L);
    }

    @Test
    void deleteTechnologiesByCapacitiesWithoutOrphansTest() {
        List<Long> capacityIds = List.of(3L);

        when(technologyPersistencePort.deleteCapacityTechnologyRelations(capacityIds))
                .thenReturn(Mono.empty());

        when(technologyPersistencePort.findOrphanedTechnologies(capacityIds))
                .thenReturn(Flux.empty());

        StepVerifier.create(
                        technologyUseCase.deleteTechnologiesByCapacities(capacityIds)
                )
                .verifyComplete();

        verify(technologyPersistencePort)
                .deleteCapacityTechnologyRelations(capacityIds);
        verify(technologyPersistencePort)
                .findOrphanedTechnologies(capacityIds);
        verify(technologyPersistencePort, never())
                .deleteTechnology(any());
    }

    @Test
    void deleteTechnologiesByCapacitiesFailsOnDeleteRelationsTest() {
        List<Long> capacityIds = List.of(1L);
        RuntimeException error = new RuntimeException("DB error");

        when(technologyPersistencePort.deleteCapacityTechnologyRelations(capacityIds))
                .thenReturn(Mono.error(error));

        when(technologyPersistencePort.findOrphanedTechnologies(any()))
                .thenReturn(Flux.empty());

        StepVerifier.create(
                        technologyUseCase.deleteTechnologiesByCapacities(capacityIds)
                )
                .expectErrorMatches(ex -> ex.equals(error))
                .verify();

        verify(technologyPersistencePort)
                .deleteCapacityTechnologyRelations(capacityIds);

        verify(technologyPersistencePort)
                .findOrphanedTechnologies(capacityIds);

        verify(technologyPersistencePort, never())
                .deleteTechnology(anyLong());
    }

    @Test
    void deleteTechnologiesByCapacitiesFailsOnDeleteTechnologyTest() {
        List<Long> capacityIds = List.of(1L);

        when(technologyPersistencePort.deleteCapacityTechnologyRelations(capacityIds))
                .thenReturn(Mono.empty());

        when(technologyPersistencePort.findOrphanedTechnologies(capacityIds))
                .thenReturn(Flux.just(10L));

        when(technologyPersistencePort.deleteTechnology(10L))
                .thenReturn(Mono.error(new RuntimeException("Delete error")));

        StepVerifier.create(
                        technologyUseCase.deleteTechnologiesByCapacities(capacityIds)
                )
                .expectError()
                .verify();

        verify(technologyPersistencePort)
                .deleteCapacityTechnologyRelations(capacityIds);
        verify(technologyPersistencePort)
                .findOrphanedTechnologies(capacityIds);
        verify(technologyPersistencePort)
                .deleteTechnology(10L);
    }
}
