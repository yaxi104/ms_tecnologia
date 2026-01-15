package com.reactivo.tecnologia.infrastructure.config;

import com.reactivo.tecnologia.infrastructure.adapters.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static com.reactivo.tecnologia.infrastructure.entrypoints.util.Constants.ROLE_ADMIN;

@EnableWebFluxSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/public/**").permitAll()
                        .pathMatchers("/tecnologia/**").hasRole(ROLE_ADMIN)
                        .pathMatchers("/tecnologias/**").hasRole(ROLE_ADMIN)
                        .pathMatchers("/capacidad-tecnologia/**").hasRole(ROLE_ADMIN)
                        .pathMatchers("/capacidad-tecnologias").hasRole(ROLE_ADMIN)
                        .pathMatchers("/tecnologias/capacidades").hasRole(ROLE_ADMIN)
                        .pathMatchers("/tecnologias/eliminar-por-capacidades").hasRole(ROLE_ADMIN)
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
