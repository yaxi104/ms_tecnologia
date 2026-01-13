package com.reactivo.tecnologia.infrastructure.entrypoints;

import com.reactivo.tecnologia.infrastructure.entrypoints.handler.CapacityTechnologyHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class CapacityTechnologyRouterRest {

    @Bean("capacityTechnologyRouter")
    public RouterFunction<ServerResponse> routerFunction(CapacityTechnologyHandler capacityTechnologyHandler) {
        return RouterFunctions
                .route(POST("/capacidad-tecnologia").and(accept(MediaType.APPLICATION_JSON)), capacityTechnologyHandler::saveAllCapacityTechnology)
                .andRoute(GET("/capacidad-tecnologia/{idCapacidad}").and(accept(MediaType.APPLICATION_JSON)), capacityTechnologyHandler::findAllIdTechnologyByIdCapacity)
                .andRoute(POST("/tecnologias/capacidades").and(accept(MediaType.APPLICATION_JSON)), capacityTechnologyHandler::getTechnologiesByCapacityIds);
//                .andRoute(POST("/tecnologias/capacidades/paginado").and(accept(MediaType.APPLICATION_JSON)), capacityTechnologyHandler::getTechnologiesByCapacityIdsPaged);
    }
}
