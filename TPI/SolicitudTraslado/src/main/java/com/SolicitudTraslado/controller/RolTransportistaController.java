package com.SolicitudTraslado.controller;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SolicitudTraslado.domain.Camion;
import com.SolicitudTraslado.domain.Tramos;
import com.SolicitudTraslado.services.CamionService;
import com.SolicitudTraslado.services.TramoService;

/**
 * Controlador para operaciones del ROL TRANSPORTISTA (Camionero/Chofer)
 * - Ver tramos asignados a su camión
 * - Registrar inicio de tramo
 * - Registrar fin de tramo
 */
@RestController
@RequestMapping("/api/transportista")
public class RolTransportistaController {

    private final TramoService tramoService;
    private final CamionService camionService;

    public RolTransportistaController(TramoService tramoService, CamionService camionService) {
        this.tramoService = tramoService;
        this.camionService = camionService;
    }

    /**
     * Obtener todos los tramos asignados al camión del transportista
     * GET /api/transportista/mis-tramos
     */
    @GetMapping("/mis-tramos")
    @PreAuthorize("hasRole('TRANSPORTISTA')")
    public ResponseEntity<?> obtenerMisTramos(@AuthenticationPrincipal Jwt jwt) {
        try {
            // Extraer el dominio del camión del JWT
            // Asumimos que Keycloak incluye el dominio del camión como claim
            String dominoCamion = jwt.getClaim("dominio_camion");

            if (dominoCamion == null || dominoCamion.trim().isEmpty()) {
                // Alternativa: usar username o dni para buscar el camión
                String username = jwt.getClaimAsString("preferred_username");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error",
                                "No se pudo identificar el camión del transportista. Username: " + username));
            }

            // Obtener tramos del camión
            List<Tramos> tramos = tramoService.obtenerTramosPorCamionDominio(dominoCamion);

            return ResponseEntity.ok(Map.of(
                    "dominoCamion", dominoCamion,
                    "tramos", tramos,
                    "cantidad", tramos.size()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener tramos: " + e.getMessage()));
        }
    }

    /**
     * Obtener un tramo específico por dominio del camión
     * GET /api/transportista/tramos/{dominio}
     */
    @GetMapping("/tramos/{dominio}")
    @PreAuthorize("hasRole('TRANSPORTISTA')")
    public ResponseEntity<?> obtenerTramosPorDominio(
            @PathVariable String dominio,
            @AuthenticationPrincipal Jwt jwt) {

        try {
            // Verificar que el dominio corresponda al transportista autenticado
            String dominoCamionToken = jwt.getClaim("dominio_camion");

            if (dominoCamionToken != null && !dominoCamionToken.equals(dominio)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No tiene permisos para ver tramos de este camión"));
            }

            List<Tramos> tramos = tramoService.obtenerTramosPorCamionDominio(dominio);
            return ResponseEntity.ok(tramos);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener tramos"));
        }
    }

    /**
     * Iniciar un tramo (registrar fecha de inicio)
     * PUT /api/transportista/tramos/{id}/iniciar
     */
    @PutMapping("/tramos/{id}/iniciar")
    @PreAuthorize("hasRole('TRANSPORTISTA')")
    public ResponseEntity<?> iniciarTramo(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        try {
            // Obtener el tramo
            Tramos tramo = tramoService.obtenerTramoPorId(id);
            if (tramo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Tramo no encontrado"));
            }

            // Verificar que el tramo pertenece al transportista autenticado
            String dominoCamionToken = jwt.getClaim("dominio_camion");
            if (dominoCamionToken != null &&
                    !tramo.getCamion().getDominio().equals(dominoCamionToken)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Este tramo no está asignado a su camión"));
            }

            // Verificar que el tramo no haya sido iniciado
            if (tramo.getFechaInicio() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El tramo ya fue iniciado previamente"));
            }

            // Registrar fecha de inicio (ahora)
            Date fechaInicio = new Date(System.currentTimeMillis());
            tramo.setFechaInicio(fechaInicio);

            Tramos actualizado = tramoService.actualizarTramo(tramo);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Tramo iniciado exitosamente",
                    "tramoId", actualizado.getId(),
                    "fechaInicio", actualizado.getFechaInicio(),
                    "origen", actualizado.getOrigen().getCiudad().getNombre(),
                    "destino", actualizado.getDestino().getCiudad().getNombre()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al iniciar tramo: " + e.getMessage()));
        }
    }

    /**
     * Finalizar un tramo (registrar fecha de fin)
     * PUT /api/transportista/tramos/{id}/finalizar
     */
    @PutMapping("/tramos/{id}/finalizar")
    @PreAuthorize("hasRole('TRANSPORTISTA')")
    public ResponseEntity<?> finalizarTramo(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        try {
            // Obtener el tramo
            Tramos tramo = tramoService.obtenerTramoPorId(id);
            if (tramo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Tramo no encontrado"));
            }

            // Verificar que el tramo pertenece al transportista autenticado
            String dominoCamionToken = jwt.getClaim("dominio_camion");
            if (dominoCamionToken != null &&
                    !tramo.getCamion().getDominio().equals(dominoCamionToken)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Este tramo no está asignado a su camión"));
            }

            // Verificar que el tramo fue iniciado
            if (tramo.getFechaInicio() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El tramo debe ser iniciado antes de finalizarlo"));
            }

            // Verificar que el tramo no haya sido finalizado
            if (tramo.getFechaFin() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El tramo ya fue finalizado previamente"));
            }

            // Registrar fecha de fin (ahora)
            Date fechaFin = new Date(System.currentTimeMillis());
            Tramos actualizado = tramoService.actualizarFechaFin(id, fechaFin);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Tramo finalizado exitosamente",
                    "tramoId", actualizado.getId(),
                    "fechaInicio", actualizado.getFechaInicio(),
                    "fechaFin", actualizado.getFechaFin(),
                    "origen", actualizado.getOrigen().getCiudad().getNombre(),
                    "destino", actualizado.getDestino().getCiudad().getNombre()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al finalizar tramo: " + e.getMessage()));
        }
    }

    /**
     * Obtener información del camión del transportista
     * GET /api/transportista/mi-camion
     */
    @GetMapping("/mi-camion")
    @PreAuthorize("hasRole('TRANSPORTISTA')")
    public ResponseEntity<?> obtenerMiCamion(@AuthenticationPrincipal Jwt jwt) {
        try {
            String dominoCamion = jwt.getClaim("dominio_camion");

            if (dominoCamion == null || dominoCamion.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "No se pudo identificar el camión del transportista"));
            }

            Camion camion = camionService.obtenerCamionPorDominio(dominoCamion);

            if (camion == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Camión no encontrado"));
            }

            return ResponseEntity.ok(camion);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener información del camión"));
        }
    }
}