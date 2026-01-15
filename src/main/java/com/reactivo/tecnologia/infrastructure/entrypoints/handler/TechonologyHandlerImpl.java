package com.reactivo.tecnologia.infrastructure.entrypoints.handler;

import com.reactivo.tecnologia.domain.api.TechnologyServicePort;
import com.reactivo.tecnologia.domain.enums.TechnicalMessage;
import com.reactivo.tecnologia.domain.exceptions.BusinessException;
import com.reactivo.tecnologia.domain.exceptions.TechnicalException;
import com.reactivo.tecnologia.infrastructure.entrypoints.dto.request.IdsRequest;
import com.reactivo.tecnologia.infrastructure.entrypoints.dto.request.TechnologyDTO;
import com.reactivo.tecnologia.infrastructure.entrypoints.dto.response.TechnologyResponse;
import com.reactivo.tecnologia.infrastructure.entrypoints.dto.response.TechnologySummaryResponse;
import com.reactivo.tecnologia.infrastructure.entrypoints.mapper.TechnologyMapper;
import com.reactivo.tecnologia.infrastructure.entrypoints.mapper.TechnologyResponseMapper;
import com.reactivo.tecnologia.infrastructure.entrypoints.mapper.TechnologySummaryResponseMapper;
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
public class TechonologyHandlerImpl {

    private final TechnologyServicePort technologyServicePort;
    private final TechnologyMapper technologyMapper;
    private final TechnologyResponseMapper technologyResponseMapper;
    private final TechnologySummaryResponseMapper technologySummaryResponseMapper;
    private final HandlerUtils handlerUtils;

    public Mono<ServerResponse> createTechnology(ServerRequest request) {
        String messageId = handlerUtils.getMessageId(request);

        return request.bodyToMono(TechnologyDTO.class)
                .flatMap(technologyDTO ->
                        technologyServicePort.saveTechnology(
                                        technologyMapper.technologyDTOToTechnology(technologyDTO))
                                .doOnSuccess(savedUser ->
                                        log.info("Technology created successfully with messageId: {}", messageId))
                )
                .flatMap(savedTechnology ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .bodyValue(TechnicalMessage.TECHNOLOGY_CREATED.getMessage()))
                .contextWrite(Context.of(X_MESSAGE_ID, messageId))
                .doOnError(ex -> log.error("Unexpected error", ex))
                .onErrorResume(BusinessException.class, ex ->
                        handlerUtils.buildErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                messageId,
                                TechnicalMessage.INVALID_PARAMETERS,
                                List.of(ErrorDTO.builder()
                                        .code(ex.getTechnicalMessage().getCode())
                                        .message(ex.getTechnicalMessage().getMessage())
                                        .param(ex.getTechnicalMessage().getParam())
                                        .build())))
                .onErrorResume(TechnicalException.class, ex ->
                        handlerUtils.buildErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                messageId,
                                TechnicalMessage.INTERNAL_ERROR,
                                List.of(ErrorDTO.builder()
                                        .code(ex.getTechnicalMessage().getCode())
                                        .message(ex.getTechnicalMessage().getMessage())
                                        .param(ex.getTechnicalMessage().getParam())
                                        .build())))
                .onErrorResume(ex -> {
                    log.error("Unexpected error occurred for messageId: {}", messageId, ex);
                    return handlerUtils.buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            messageId,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build()));
                });
    }

    public Mono<ServerResponse> getAllTechnologies(ServerRequest request) {
        String messageId = handlerUtils.getMessageId(request);

        Flux<TechnologyResponse> dtoFlux = technologyServicePort.findAll()
                .map(technologyResponseMapper::toDto);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dtoFlux, TechnologyResponse.class)
                .contextWrite(Context.of(X_MESSAGE_ID, messageId))
                .doOnError(ex -> log.error("Error fetching technologies for messageId: {}", messageId, ex))
                .onErrorResume(ex -> handlerUtils.buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        messageId,
                        TechnicalMessage.INTERNAL_ERROR,
                        List.of(ErrorDTO.builder()
                                .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                .build())));
    }

    public Mono<ServerResponse> getTechnologiesByIds(ServerRequest request) {
        String messageId = handlerUtils.getMessageId(request);

        return request.bodyToMono(IdsRequest.class)
                .flatMapMany(req ->
                        technologyServicePort.findByIds(req.ids())
                )
                .map(technologySummaryResponseMapper::toDto)
                .as(dtoFlux ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(dtoFlux, TechnologySummaryResponse.class)
                )
                .contextWrite(Context.of(X_MESSAGE_ID, messageId))
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

    public Mono<ServerResponse> deleteTechnologiesByCapacities(ServerRequest request) {
        String messageId = handlerUtils.getMessageId(request);

        return request.bodyToMono(IdsRequest.class)
                .flatMap(idsRequest ->
                        technologyServicePort.deleteTechnologiesByCapacities(idsRequest.ids())
                )
                .then(ServerResponse.noContent().build())
                .contextWrite(Context.of(X_MESSAGE_ID, messageId))
                .doOnSuccess(v ->
                        log.info("Technologies and capacity-technology relations deleted successfully. messageId={}", messageId))
                .onErrorResume(BusinessException.class, ex ->
                        handlerUtils.buildErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                messageId,
                                TechnicalMessage.INVALID_PARAMETERS,
                                List.of(ErrorDTO.builder()
                                        .code(ex.getTechnicalMessage().getCode())
                                        .message(ex.getTechnicalMessage().getMessage())
                                        .param(ex.getTechnicalMessage().getParam())
                                        .build())))
                .onErrorResume(TechnicalException.class, ex ->
                        handlerUtils.buildErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                messageId,
                                TechnicalMessage.INTERNAL_ERROR,
                                List.of(ErrorDTO.builder()
                                        .code(ex.getTechnicalMessage().getCode())
                                        .message(ex.getTechnicalMessage().getMessage())
                                        .param(ex.getTechnicalMessage().getParam())
                                        .build())))
                .onErrorResume(ex -> {
                    log.error("Unexpected error occurred for messageId: {}", messageId, ex);
                    return handlerUtils.buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            messageId,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build()));
                });
    }

}
