package com.reactivo.tecnologia.infrastructure.config;

import com.reactivo.tecnologia.domain.api.JwtServicePort;
import com.reactivo.tecnologia.infrastructure.adapters.security.JwtServiceAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public JwtServicePort jwtServicePort(
            @Value("${jwt.secret}") String secret) {
        return new JwtServiceAdapter(secret);
    }
}