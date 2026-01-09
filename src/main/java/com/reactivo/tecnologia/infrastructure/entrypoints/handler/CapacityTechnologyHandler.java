package com.reactivo.tecnologia.infrastructure.entrypoints.handler;

import com.reactivo.tecnologia.domain.api.CapacityTechnologyServicePort;
import com.reactivo.tecnologia.domain.enums.TechnicalMessage;
import com.reactivo.tecnologia.domain.model.CapacityTechnology;
import com.reactivo.tecnologia.infrastructure.entrypoints.util.ErrorDTO;
import com.reactivo.tecnologia.infrastructure.entrypoints.util.HandlerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.List;

import static com.reactivo.tecnologia.infrastructure.entrypoints.util.Constants.X_MESSAGE_ID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CapacityTechnologyHandler {

    private final CapacityTechnologyServicePort capacityTechnologyUseCase;
    private final HandlerUtils handlerUtils;

    public Mono<ServerResponse> saveAllCapacityTechnology(ServerRequest request) {
        String messageId = handlerUtils.getMessageId(request);

        Flux<CapacityTechnology> capacityTechFlux = request.bodyToFlux(CapacityTechnology.class);

        return capacityTechnologyUseCase.saveAllCapacityTechnology(capacityTechFlux)
                .collectList()
                .flatMap(list -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(list))
                .contextWrite(Context.of(X_MESSAGE_ID, messageId))
                .doOnError(ex -> log.error("Error saving capacity technologies for messageId: {}", messageId, ex))
                .onErrorResume(ex -> handlerUtils.buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        messageId,
                        TechnicalMessage.INTERNAL_ERROR,
                        List.of(ErrorDTO.builder()
                                .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                .build())));
    }


    public Mono<ServerResponse> findAllIdTechnologyByIdCapacity(ServerRequest request) {
        String messageId = handlerUtils.getMessageId(request);
        Long idCapacity = Long.valueOf(request.pathVariable("idCapacity"));

        return capacityTechnologyUseCase.findAllIdTechnologyByIdCapacity(idCapacity)
                .collectList()
                .flatMap(list -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(list))
                .contextWrite(Context.of(X_MESSAGE_ID, messageId))
                .doOnError(ex -> log.error("Error fetching technology IDs for capacity {} messageId: {}", idCapacity, messageId, ex))
                .onErrorResume(ex -> handlerUtils.buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        messageId,
                        TechnicalMessage.INTERNAL_ERROR,
                        List.of(ErrorDTO.builder()
                                .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                .build())));
    }

}