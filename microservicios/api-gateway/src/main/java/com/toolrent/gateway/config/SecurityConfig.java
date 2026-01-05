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
                // Actuator público para health checks
                .pathMatchers("/actuator/**").permitAll()
                // GET requests públicos (lectura sin autenticación)
                .pathMatchers(HttpMethod.GET, "/api/v1/**").permitAll()
                // POST, PUT, DELETE requieren autenticación
                .pathMatchers(HttpMethod.POST, "/api/v1/**").authenticated()
                .pathMatchers(HttpMethod.PUT, "/api/v1/**").authenticated()
                .pathMatchers(HttpMethod.DELETE, "/api/v1/**").authenticated()
                // Cualquier otra petición requiere autenticación
                .anyExchange().authenticated()
            )
            // Validación de JWT desde Keycloak
            .oauth2ResourceServer(oauth2 -> oauth2.jwt());

        return http.build();
    }
}

