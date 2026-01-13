package com.reactivo.tecnologia.infrastructure.entrypoints.handler;

import com.reactivo.tecnologia.domain.api.CapacityTechnologyServicePort;
import com.reactivo.tecnologia.domain.enums.TechnicalMessage;
import com.reactivo.tecnologia.domain.model.CapacityTechnology;
import com.reactivo.tecnologia.infrastructure.entrypoints.dto.request.CapacityIdsRequest;
import com.reactivo.tecnologia.infrastructure.entrypoints.dto.request.CapacityTechnologyDTO;
import com.reactivo.tecnologia.infrastructure.entrypoints.mapper.CapacityTechnologyMapper;
import com.reactivo.tecnologia.infrastructure.entrypoints.util.ContextKeys;
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
import java.util.Objects;

import static com.reactivo.tecnologia.infrastructure.entrypoints.util.Constants.HEADER_REQUIRED;
import static com.reactivo.tecnologia.infrastructure.entrypoints.util.Constants.X_MESSAGE_ID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CapacityTechnologyHandler {

    private final CapacityTechnologyServicePort capacityTechnologyUseCase;
    private final CapacityTechnologyMapper mapper;
    private final HandlerUtils handlerUtils;

    public Mono<ServerResponse> saveAllCapacityTechnology(ServerRequest request) {

        String messageId = handlerUtils.getMessageId(request);
        if (messageId == null) {
            return handlerUtils.buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    null,
                    TechnicalMessage.INVALID_PARAMETERS,
                    List.of(ErrorDTO.builder()
                            .code(TechnicalMessage.INVALID_PARAMETERS.getCode())
                            .message(HEADER_REQUIRED)
                            .build()));
        }

        Flux<CapacityTechnology> capacityFlux =
                request.bodyToFlux(CapacityTechnologyDTO.class)
                        .map(mapper::toModel)
                        .filter(Objects::nonNull);

        return capacityTechnologyUseCase.saveAllCapacityTechnology(capacityFlux)
                .collectList()
                .flatMap(list ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(list))
                .contextWrite(ctx -> ctx.put(ContextKeys.X_MESSAGE_ID, messageId))
                .doOnSuccess(v ->
                        log.info("CapacityTechnology saved successfully. messageId={}", messageId))
                .doOnError(ex ->
                        log.error("Error saving CapacityTechnology. messageId={}", messageId, ex))
                .onErrorResume(ex ->
                        handlerUtils.buildErrorResponse(
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
        if (messageId == null) {
            return handlerUtils.buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    null,
                    TechnicalMessage.INVALID_PARAMETERS,
                    List.of(ErrorDTO.builder()
                            .code(TechnicalMessage.INVALID_PARAMETERS.getCode())
                            .message(HEADER_REQUIRED)
                            .build()));
        }

        Long idCapacity = Long.valueOf(request.pathVariable("idCapacity"));

        return capacityTechnologyUseCase.findAllIdTechnologyByIdCapacity(idCapacity)
                .collectList()
                .flatMap(list ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(list))
                .contextWrite(ctx -> ctx.put(ContextKeys.X_MESSAGE_ID, messageId))
                .doOnError(ex ->
                        log.error("Error fetching technologies. capacityId={} messageId={}",
                                idCapacity, messageId, ex))
                .onErrorResume(ex ->
                        handlerUtils.buildErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                messageId,
                                TechnicalMessage.INTERNAL_ERROR,
                                List.of(ErrorDTO.builder()
                                        .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                        .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                        .build())));
    }

    public Mono<ServerResponse> getTechnologiesByCapacityIds(ServerRequest request) {
        String messageId = handlerUtils.getMessageId(request);

        return request.bodyToMono(CapacityIdsRequest.class)
                .flatMap(req ->
                        capacityTechnologyUseCase.findTechnologiesByCapacityIds(req.capacityIds())
                )
                .flatMap(resultMap ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(resultMap)
                )
                .contextWrite(Context.of(X_MESSAGE_ID, messageId))
                .doOnError(ex ->
                        log.error("Error fetching technologies by capacity ids, messageId: {}", messageId, ex))
                .onErrorResume(ex ->
                        handlerUtils.buildErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                messageId,
                                TechnicalMessage.INTERNAL_ERROR,
                                List.of(ErrorDTO.builder()
                                        .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                        .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                        .build())));
    }

    public Mono<ServerResponse> getCapacityIdGroupedTechnologies(ServerRequest request) {

        // Obtener X-MESSAGE-ID del header
        String messageId = handlerUtils.getMessageId(request);
        if (messageId == null) {
            return handlerUtils.buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    null,
                    TechnicalMessage.INVALID_PARAMETERS,
                    List.of(ErrorDTO.builder()
                            .code(TechnicalMessage.INVALID_PARAMETERS.getCode())
                            .message(HEADER_REQUIRED)
                            .build()));
        }

        // Leer query params
        int page = request.queryParam("page").map(Integer::parseInt).orElse(0);
        int size = request.queryParam("size").map(Integer::parseInt).orElse(10);
        boolean asc = request.queryParam("asc").map(Boolean::parseBoolean).orElse(true);

        // Llamar al use case que devuelve Mono<Map<Long, List<TechnologySummary>>>
        return capacityTechnologyUseCase
                .getCapacidtyIdGroupedTechnologies(page, size, asc)
                .flatMap(resultMap ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(resultMap)
                )
                .contextWrite(ctx -> ctx.put(ContextKeys.X_MESSAGE_ID, messageId))
                .doOnError(ex ->
                        log.error("Error fetching grouped technologies, messageId={}", messageId, ex))
                .onErrorResume(ex ->
                        handlerUtils.buildErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                messageId,
                                TechnicalMessage.INTERNAL_ERROR,
                                List.of(ErrorDTO.builder()
                                        .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                        .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                        .build())));
    }

}
