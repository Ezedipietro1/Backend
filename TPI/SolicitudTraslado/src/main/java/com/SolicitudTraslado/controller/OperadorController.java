package com.SolicitudTraslado.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

import com.SolicitudTraslado.domain.Camion;
import com.SolicitudTraslado.domain.Ciudad;
import com.SolicitudTraslado.domain.Contenedor;
import com.SolicitudTraslado.domain.Deposito;
import com.SolicitudTraslado.domain.Tarifa;
import com.SolicitudTraslado.domain.Tramos;
import com.SolicitudTraslado.services.CamionService;
import com.SolicitudTraslado.services.CiudadService;
import com.SolicitudTraslado.services.ContenedorService;
import com.SolicitudTraslado.services.DepositoService;
import com.SolicitudTraslado.services.TarifaService;
import com.SolicitudTraslado.services.TramoService;

/**
 * Controlador para operaciones del ROL OPERADOR/ADMINISTRADOR
 * - Gestionar ciudades, depósitos, tarifas, camiones, contenedores
 * - Asignar camiones a tramos
 * - Modificar parámetros del sistema
 */
@RestController
@RequestMapping("/api/operador")
public class OperadorController {

    private final CiudadService ciudadService;
    private final DepositoService depositoService;
    private final TarifaService tarifaService;
    private final CamionService camionService;
    private final ContenedorService contenedorService;
    private final TramoService tramoService;

    public OperadorController(CiudadService ciudadService,
            DepositoService depositoService,
            TarifaService tarifaService,
            CamionService camionService,
            ContenedorService contenedorService,
            TramoService tramoService) {
        this.ciudadService = ciudadService;
        this.depositoService = depositoService;
        this.tarifaService = tarifaService;
        this.camionService = camionService;
        this.contenedorService = contenedorService;
        this.tramoService = tramoService;
    }

    // ==================== GESTIÓN DE CIUDADES ====================

    @PostMapping("/ciudades")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<?> crearCiudad(@RequestBody Ciudad ciudad) {
        try {
            Ciudad creada = ciudadService.crearCiudad(ciudad);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/ciudades/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<?> actualizarCiudad(
            @PathVariable Long id,
            @RequestBody Ciudad ciudad) {
        try {
            ciudad.setId(id);
            Ciudad actualizada = ciudadService.actualizarCiudad(ciudad);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/ciudades")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<?> listarCiudades() {
        List<Ciudad> ciudades = ciudadService.listarCiudades();
        return ResponseEntity.ok(ciudades);
    }

    @GetMapping("/ciudades/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<?> obtenerCiudad(@PathVariable Long id) {
        try {
            Ciudad ciudad = ciudadService.obtenerCiudadPorId(id);
            return ResponseEntity.ok(ciudad);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== GESTIÓN DE DEPÓSITOS ====================

    @PostMapping("/depositos")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<?> crearDeposito(@RequestBody Deposito deposito) {
        try {
            Deposito creado = depositoService.crearDeposito(deposito);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/depositos/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<?> actualizarDeposito(
            @PathVariable Long id,
            @RequestBody Deposito deposito) {
        try {
            deposito.setId(id);
            Deposito actualizado = depositoService.actualizarDeposito(deposito);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/depositos")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<?> listarDepositos() {
        Map<Long, Deposito> depositos = depositoService.listarDepositos();
        return ResponseEntity.ok(depositos);
    }

    // ==================== GESTIÓN DE TARIFAS ====================

    @PostMapping("/tarifas")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<?> crearTarifa(@RequestBody Tarifa tarifa) {
        try {
            Tarifa creada = tarifaService.crearTarifa(tarifa);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/tarifas/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<?> modificarTarifa(
            @PathVariable Long id,
            @RequestBody Tarifa tarifa) {
        try {
            tarifa.setId(id);
            Tarifa actualizada = tarifaService.actualizarTarifa(tarifa);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/tarifas")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<?> listarTarifas() {
        Map<Long, Tarifa> tarifas = tarifaService.listarTarifas();
        return ResponseEntity.ok(tarifas);
    }

    // ==================== GESTIÓN DE CAMIONES ====================

    @PostMapping("/camiones")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<?> crearCamion(@RequestBody Camion camion, @RequestHeader("Authorization") String authHeader) {
        try {
            Camion creado = camionService.crearCamion(camion, authHeader);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/camiones/{dominio}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<?> actualizarCamion(
            @PathVariable String dominio,
            @RequestBody Camion camion) {
        try {
            Camion actualizado = camionService.actualizarCamion(camion, dominio);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/camiones")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<?> listarCamiones() {
        List<Map<String, Object>> camiones = camionService.listarCamiones();
        return ResponseEntity.ok(camiones);
    }

    // ==================== GESTIÓN DE CONTENEDORES ====================

    @PostMapping("/contenedores")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<?> crearContenedor(@RequestBody Contenedor contenedor) {
        try {
            Contenedor creado = contenedorService.crearContenedor(contenedor);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/contenedores/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<?> actualizarContenedor(
            @PathVariable Long id,
            @RequestBody Contenedor contenedor) {
        try {
            Contenedor actualizado = contenedorService.actualizarContenedor(contenedor, id);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/contenedores")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<?> listarContenedores() {
        Map<Long, Contenedor> contenedores = contenedorService.listarContenedores();
        return ResponseEntity.ok(contenedores);
    }

    // ==================== ASIGNACIÓN DE TRAMOS ====================

    @PutMapping("/tramos/{idTramo}/asignar")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<?> asignarCamionATramo(
            @PathVariable Long idTramo,
            @RequestParam String dominoCamion) {
        try {
            // Obtener el tramo
            Tramos tramo = tramoService.obtenerTramoPorId(idTramo);
            if (tramo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Tramo no encontrado"));
            }

            // Obtener el camión
            Camion camion = camionService.obtenerCamionPorDominio(dominoCamion);
            if (camion == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Camión no encontrado"));
            }

            // Asignar camión al tramo
            tramo.setCamion(camion);
            Tramos actualizado = tramoService.actualizarTramo(tramo);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Camión asignado exitosamente",
                    "tramoId", actualizado.getId(),
                    "camionDominio", camion.getDominio()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/tramos")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<?> listarTodosLosTramos() {
        Map<Long, Tramos> tramos = tramoService.listarTramos();
        return ResponseEntity.ok(tramos);
    }
}
