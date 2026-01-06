package com.toolrent.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchanges -> exchanges
                // TEMPORAL: Permitir todas las peticiones sin autenticación para debug
                .anyExchange().permitAll()
            );
            // TEMPORAL: Comentar OAuth2 para evitar errores de conexión con Keycloak
            // .oauth2ResourceServer(oauth2 -> oauth2.jwt());

        return http.build();
    }
}

