package com.Gateway.Gateway.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${seguridad.desactivado:false}")
    private boolean seguridadDesactivado;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        if (seguridadDesactivado) {
            return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(exchange -> exchange.anyExchange().permitAll())
                    .build();
        }
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        // Endpoints públicos
                        .pathMatchers("/api/auth/**", "/api/public/**").permitAll()

                        // ENDPOINTS PARA CLIENTE
                        .pathMatchers(HttpMethod.POST, "/api/solicitudes/**").hasRole("CLIENTE")
                        .pathMatchers(HttpMethod.GET, "/api/solicitudes/{numero}/**").hasRole("CLIENTE")
                        .pathMatchers(HttpMethod.GET, "/api/contenedores/{id}/**").hasRole("CLIENTE")

                        // ENDPOINTS PARA OPERADOR
                        .pathMatchers("/api/ciudades/**").hasRole("OPERADOR")
                        .pathMatchers("/api/depositos/**").hasRole("OPERADOR")
                        .pathMatchers("/api/tarifas/**").hasRole("OPERADOR")
                        .pathMatchers("/api/camiones/**").hasRole("OPERADOR")
                        .pathMatchers("/api/contenedores/**").hasRole("OPERADOR")
                        // .requestMatchers("/api/tramos/asignar/**").hasRole("OPERADOR")

                        // ENDPOINTS PARA TRANSPORTISTA
                        .pathMatchers(HttpMethod.GET, "/api/tramos/{dominio}").hasRole("TRANSPORTISTA")
                        .pathMatchers(HttpMethod.PUT, "/api/tramos/{id}/**").hasRole("TRANSPORTISTA")

                        // Cualquier otra petición requiere autenticación
                        .anyExchange().authenticated()
                    )
                    .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                    );

        return http.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> roles = extractRealmRoles(jwt);
            List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
            return Flux.fromIterable(authorities);
        });
        return converter;
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null || !realmAccess.containsKey("roles")) {
            return Collections.emptyList();
        }
        return (List<String>) realmAccess.get("roles");
    }
}
