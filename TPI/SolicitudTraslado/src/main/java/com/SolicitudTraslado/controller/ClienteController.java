package com.SolicitudTraslado.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SolicitudTraslado.domain.Contenedor;
import com.SolicitudTraslado.domain.SolicitudTraslado;
import com.SolicitudTraslado.services.ContenedorService;
import com.SolicitudTraslado.services.SolicitudTrasladoService;
import com.SolicitudTraslado.services.TarifaService;

/**
 * Controlador para operaciones del ROL CLIENTE
 * - Crear solicitudes de traslado
 * - Consultar sus propias solicitudes
 * - Ver seguimiento de contenedores
 * - Calcular costos estimados
 */
@RestController
@RequestMapping("/api/cliente")
public class ClienteController {

    private final SolicitudTrasladoService solicitudTrasladoService;
    private final ContenedorService contenedorService;
    private final TarifaService tarifaService;

    public ClienteController(SolicitudTrasladoService solicitudTrasladoService,
            ContenedorService contenedorService,
            TarifaService tarifaService) {
        this.solicitudTrasladoService = solicitudTrasladoService;
        this.contenedorService = contenedorService;
        this.tarifaService = tarifaService;
    }

    /**
     * Crear una nueva solicitud de traslado
     * POST /api/cliente/solicitudes
     */
    @PostMapping("/solicitudes")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> crearSolicitud(
            @RequestBody SolicitudTraslado solicitud,
            @AuthenticationPrincipal Jwt jwt) {

        try {
            // Extraer el clienteId del JWT (asumiendo que Keycloak lo incluye como claim)
            Long clienteId = jwt.getClaim("client_id");
            if (clienteId == null) {
                // Alternativa: usar el username y buscar el cliente en el microservicio
                String username = jwt.getClaimAsString("preferred_username");
                // Aquí deberías llamar al microservicio de Clientes para obtener el ID
                // Por ahora, lanzamos error si no viene en el token
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "No se pudo identificar el cliente del token"));
            }

            // Asociar la solicitud con el cliente autenticado
            solicitud.setClienteId(clienteId);

            // Crear la solicitud
            SolicitudTraslado creada = solicitudTrasladoService.actualizarSolicitudTraslado(solicitud);

            return ResponseEntity.status(HttpStatus.CREATED).body(creada);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear la solicitud: " + e.getMessage()));
        }
    }

    /**
     * Obtener detalles de una solicitud específica (solo si pertenece al cliente)
     * GET /api/cliente/solicitudes/{numero}
     */
    @GetMapping("/solicitudes/{numero}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> obtenerSolicitud(
            @PathVariable Long numero,
            @AuthenticationPrincipal Jwt jwt) {

        try {
            Long clienteId = jwt.getClaim("client_id");
            SolicitudTraslado solicitud = solicitudTrasladoService.obtenerSolicitudPorNumero(numero);

            // Verificar que la solicitud pertenece al cliente autenticado
            if (!solicitud.getClienteId().equals(clienteId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No tiene permisos para ver esta solicitud"));
            }

            return ResponseEntity.ok(solicitud);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener la solicitud"));
        }
    }

    /**
     * Seguimiento del contenedor (estado actual)
     * GET /api/cliente/contenedores/{id}/seguimiento
     */
    @GetMapping("/contenedores/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> seguimientoContenedor(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        try {
            Long clienteId = jwt.getClaim("client_id");

            // Obtener el contenedor
            Contenedor contenedor = contenedorService.obtenerContenedorPorId(id);

            // Verificar que el contenedor pertenece a una solicitud del cliente
            // (Necesitarías agregar un método en el servicio para esto)

            return ResponseEntity.ok(Map.of(
                    "contenedorId", contenedor.getId(),
                    "estado", contenedor.getEstadoContenedor(),
                    "volumen", contenedor.getVolumen(),
                    "peso", contenedor.getPeso()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener seguimiento"));
        }
    }

}
