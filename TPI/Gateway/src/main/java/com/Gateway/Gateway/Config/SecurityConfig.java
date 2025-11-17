package com.Gateway.Gateway.config;

import org.springframework.beans.factory.annotation.Value; // Importado
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator; // Importado
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder; // Importado
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder; // Importado
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    // Inyectamos la URL del JWKS desde el application.properties
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .authorizeExchange(exchange -> exchange
                // Endpoints públicos (Swagger, health, login, clientes, y búsqueda)
                .pathMatchers(
                    "/",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/webjars/**",
                    "/actuator/health",
                    "/login",
                    "/logout",
                    "/api/clientes/**", // API de Clientes es pública
                    "/api/solicitudes/ciudades/search" // Endpoint público de búsqueda
                ).permitAll()
                // Todas las demás peticiones (ej. /api/solicitudes/**) requieren autenticación
                .anyExchange().authenticated()
            )
            .oauth2Login(Customizer.withDefaults())
            .oauth2ResourceServer(oauth2 -> oauth2
                // Spring usará nuestro bean 'jwtDecoder()' personalizado
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    /**
     * NUEVO BEAN: Este es el arreglo para el 401 (versión Reactiva).
     */
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        NimbusReactiveJwtDecoder reactiveJwtDecoder = NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
        // Validamos solo la fecha de expiración, no el 'issuer'
        reactiveJwtDecoder.setJwtValidator(new JwtTimestampValidator());
        return reactiveJwtDecoder;
    }

    /**
     * Conversor de roles (sin cambios)
     */
    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        Converter<Jwt, Collection<GrantedAuthority>> authoritiesConverter = jwt -> {
            Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");

            if (realmAccess == null || realmAccess.isEmpty()) {
                return List.of();
            }

            Collection<String> roles = (Collection<String>) realmAccess.get("roles");

            return roles.stream()
                    .map(roleName -> "ROLE_" + roleName.toUpperCase())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        };
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> Flux.fromIterable(authoritiesConverter.convert(jwt)));

        return converter;
    }
}