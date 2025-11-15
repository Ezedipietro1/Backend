package com.SolicitudTraslado.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers("/api/auth/**", "/api/public/**").permitAll()

                        // ENDPOINTS PARA CLIENTE
                        .requestMatchers(HttpMethod.POST, "/api/solicitudes/**").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/solicitudes/{numero}/**").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/contenedores/{id}/**").hasRole("CLIENTE")

                        // ENDPOINTS PARA OPERADOR
                        .requestMatchers("/api/ciudades/**").hasRole("OPERADOR")
                        .requestMatchers("/api/depositos/**").hasRole("OPERADOR")
                        .requestMatchers("/api/tarifas/**").hasRole("OPERADOR")
                        .requestMatchers("/api/camiones/**").hasRole("OPERADOR")
                        .requestMatchers("/api/contenedores/**").hasRole("OPERADOR")
                        // .requestMatchers("/api/tramos/asignar/**").hasRole("OPERADOR")

                        // ENDPOINTS PARA TRANSPORTISTA
                        .requestMatchers(HttpMethod.GET, "/api/tramos/{dominio}").hasRole("TRANSPORTISTA")
                        .requestMatchers(HttpMethod.PUT, "/api/tramos/{id}/**").hasRole("TRANSPORTISTA")

                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated())
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // Keycloak envía los roles en "realm_access.roles"
        grantedAuthoritiesConverter.setAuthoritiesClaimName("realm_access.roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
}
