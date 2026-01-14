package com.reactivo.tecnologia.domain.usecase;

import com.reactivo.tecnologia.domain.exceptions.BusinessException;
import com.reactivo.tecnologia.domain.model.CapacityTechnology;
import com.reactivo.tecnologia.domain.model.TechnologySummary;
import com.reactivo.tecnologia.domain.spi.CapacityTechnologyPersistencePort;
import com.reactivo.tecnologia.domain.spi.TechnologyPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CapacityTechnologyUseCaseTest {

    @Mock
    private CapacityTechnologyPersistencePort capacityTechnologyPersistencePort;

    @Mock
    private TechnologyPersistencePort technologyPersistencePort;

    private CapacityTechnologyUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CapacityTechnologyUseCase(capacityTechnologyPersistencePort, technologyPersistencePort);
    }

    @Test
    void saveAllCapacityTechnologySuccessTest() {
        CapacityTechnology tech1 = new CapacityTechnology(1L, 100L, 200L);
        CapacityTechnology tech2 = new CapacityTechnology(2L, 101L, 201L);

        Flux<CapacityTechnology> input = Flux.just(tech1, tech2);

        when(capacityTechnologyPersistencePort.saveAll(any()))
                .thenReturn(Flux.just(tech1, tech2));

        StepVerifier.create(useCase.saveAllCapacityTechnology(input))
                .expectNext(tech1)
                .expectNext(tech2)
                .verifyComplete();

        verify(capacityTechnologyPersistencePort, times(1))
                .saveAll(any());
    }

    @Test
    void saveAllCapacityTechnologyEmptyFluxTest() {

        Flux<CapacityTechnology> input = Flux.empty();

        StepVerifier.create(useCase.saveAllCapacityTechnology(input))
                .expectError(BusinessException.class)
                .verify();

        verifyNoInteractions(capacityTechnologyPersistencePort);
    }

    @Test
    void findAllIdTechnologyByIdCapacitySuccessTest() {
        Long capacityId = 1L;

        Flux<Long> techIds = Flux.just(10L, 20L);
        when(capacityTechnologyPersistencePort.findAllIdTechnologyByIdCapacity(capacityId))
                .thenReturn(techIds);

        TechnologySummary summary1 = new TechnologySummary(10L, "Tech A");
        TechnologySummary summary2 = new TechnologySummary(20L, "Tech B");
        when(technologyPersistencePort.findByIds(List.of(10L, 20L)))
                .thenReturn(Flux.just(summary1, summary2));

        StepVerifier.create(useCase.findAllIdTechnologyByIdCapacity(capacityId))
                .expectNext(summary1)
                .expectNext(summary2)
                .verifyComplete();

        verify(capacityTechnologyPersistencePort, times(1)).findAllIdTechnologyByIdCapacity(capacityId);
        verify(technologyPersistencePort, times(1)).findByIds(List.of(10L, 20L));
    }

    @Test
    void findAllIdTechnologyByIdCapacityNullIdsTest() {
        Long capacityId = 2L;

        when(capacityTechnologyPersistencePort.findAllIdTechnologyByIdCapacity(capacityId))
                .thenReturn(Flux.empty());

        StepVerifier.create(useCase.findAllIdTechnologyByIdCapacity(capacityId))
                .verifyComplete();

        verify(capacityTechnologyPersistencePort, times(1)).findAllIdTechnologyByIdCapacity(capacityId);
        verifyNoInteractions(technologyPersistencePort);
    }

    @Test
    void findTechnologiesByCapacityIdsEmptyInputTest() {
        StepVerifier.create(useCase.findTechnologiesByCapacityIds(List.of()))
                .expectNext(Map.of())
                .verifyComplete();

        StepVerifier.create(useCase.findTechnologiesByCapacityIds(null))
                .expectNext(Map.of())
                .verifyComplete();

        verifyNoInteractions(capacityTechnologyPersistencePort);
        verifyNoInteractions(technologyPersistencePort);
    }

    @Test
    void findTechnologiesByCapacityIdsNoDataTest() {
        List<Long> capacityIds = List.of(1L, 2L);

        when(capacityTechnologyPersistencePort.findByIdCapacityIn(capacityIds))
                .thenReturn(Flux.empty());

        StepVerifier.create(useCase.findTechnologiesByCapacityIds(capacityIds))
                .expectNext(Map.of())
                .verifyComplete();

        verify(capacityTechnologyPersistencePort, times(1)).findByIdCapacityIn(capacityIds);
        verifyNoInteractions(technologyPersistencePort);
    }

    @Test
    void findTechnologiesByCapacityIdsWithDataTest() {
        List<Long> capacityIds = List.of(1L, 2L);

        CapacityTechnology ct1 = new CapacityTechnology(1L, 1L, 10L);
        CapacityTechnology ct2 = new CapacityTechnology(2L, 1L, 11L);
        CapacityTechnology ct3 = new CapacityTechnology(3L, 2L, 20L);

        when(capacityTechnologyPersistencePort.findByIdCapacityIn(capacityIds))
                .thenReturn(Flux.just(ct1, ct2, ct3));

        TechnologySummary ts1 = new TechnologySummary(10L, "Tech A");
        TechnologySummary ts2 = new TechnologySummary(11L, "Tech B");
        TechnologySummary ts3 = new TechnologySummary(20L, "Tech C");

        when(technologyPersistencePort.findByIds(anyList()))
                .thenReturn(Flux.just(ts1, ts2, ts3));

        StepVerifier.create(useCase.findTechnologiesByCapacityIds(capacityIds))
                .assertNext(map -> {
                    assertEquals(3, map.size());
                })
                .verifyComplete();

        verify(capacityTechnologyPersistencePort, times(1)).findByIdCapacityIn(capacityIds);
        verify(technologyPersistencePort, times(1)).findByIds(anyList());
    }

    @Test
    void getCapacidtyIdGroupedTechnologiesTest() {
        Map<Long, List<TechnologySummary>> expected = Map.of(
                1L, List.of(new TechnologySummary(10L, "Tech A")),
                2L, List.of(new TechnologySummary(20L, "Tech B"))
        );

        when(capacityTechnologyPersistencePort.getCapacityIdGroupedTechnologiesAsMap(0, 2, true))
                .thenReturn(Mono.just(expected));

        StepVerifier.create(useCase.getCapacidtyIdGroupedTechnologies(0, 2, true))
                .expectNext(expected)
                .verifyComplete();

        verify(capacityTechnologyPersistencePort, times(1)).getCapacityIdGroupedTechnologiesAsMap(0, 2, true);
    }

}