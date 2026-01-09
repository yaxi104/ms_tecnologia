package com.reactivo.tecnologia.infrastructure.entrypoints;

import com.reactivo.tecnologia.infrastructure.entrypoints.handler.TechonologyHandlerImpl;
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
public class TechnologyRouterRest {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(TechonologyHandlerImpl techonologyHandler) {
        return RouterFunctions
                .route(POST("/tecnologia").and(accept(MediaType.APPLICATION_JSON)), techonologyHandler::createTechnology)
                .andRoute(GET("/tecnologias").and(accept(MediaType.APPLICATION_JSON)), techonologyHandler::getAllTechnologies);
    }
}