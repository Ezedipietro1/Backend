package com.SolicitudTraslado.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.SolicitudTraslado.domain.Cliente;
import com.SolicitudTraslado.domain.Contenedor;
import com.SolicitudTraslado.domain.Ruta;
import com.SolicitudTraslado.services.RutaService;
import com.SolicitudTraslado.domain.SolicitudTraslado;
import com.SolicitudTraslado.services.SolicitudTrasladoService;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitudes/solicitudTraslado")
public class SolicitudTrasladoController {

    private final SolicitudTrasladoService solicitudTrasladoService;
    private final RutaService rutaService;

    public SolicitudTrasladoController(SolicitudTrasladoService solicitudTrasladoService, RutaService rutaService) {
        this.solicitudTrasladoService = solicitudTrasladoService;
        this.rutaService = rutaService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<SolicitudTraslado> crear(Long clienteId, String nombre, String apellido, String telefono, boolean activo, String email, Double volumen, Double peso, Long ubicacionOrigenId, Long ubicacionDestinoId) {
        SolicitudTraslado creada = solicitudTrasladoService.crearSolicitudTraslado(clienteId, nombre, apellido, telefono, activo, email, volumen, peso, ubicacionOrigenId, ubicacionDestinoId);
        return ResponseEntity.ok(creada);
    }

    @GetMapping("/{numero}")
    public ResponseEntity<SolicitudTraslado> obtenerPorId(@PathVariable Long numero) {
        SolicitudTraslado solicitud = solicitudTrasladoService.obtenerSolicitudPorNumero(numero);
        if (solicitud != null) {
            return ResponseEntity.ok(solicitud);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<Map<Long, SolicitudTraslado>> listarSolicitudes() {
        Map<Long, SolicitudTraslado> solicitudes = solicitudTrasladoService.listarSolicitudes();
        return ResponseEntity.ok(solicitudes);
    }

    @PutMapping("/{numero}")
    public ResponseEntity<SolicitudTraslado> actualizar(@PathVariable Long numero, @RequestBody SolicitudTraslado solicitud) {
        solicitud.setNumero(numero);
        SolicitudTraslado actualizada = solicitudTrasladoService.actualizarSolicitudTraslado(solicitud);
        return ResponseEntity.ok(actualizada);
    }

    @PutMapping("/{numero}/asignar_ruta/{rutaId}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<SolicitudTraslado> asignarRuta(@PathVariable Long numero, @PathVariable Long rutaId) {
        SolicitudTraslado solicitud = solicitudTrasladoService.obtenerSolicitudPorNumero(numero);
        Ruta ruta = rutaService.obtenerRutaPorId(rutaId);
        if (solicitud == null || ruta == null) {
            return ResponseEntity.notFound().build();
        }
        solicitudTrasladoService.asignarRutaASolicitud(solicitud, ruta);
        return ResponseEntity.ok(solicitud);
    }

    @PutMapping("/{numero}/asignarCamionATramo")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<SolicitudTraslado> asignarCamionATramo(
            @PathVariable Long numero,
            @RequestParam Long tramoId,
            @RequestParam String dominioCamion) {
        try {
            solicitudTrasladoService.asignarCamionATramo(numero, tramoId, dominioCamion);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{numero}/finalizarSolicitud")
    public ResponseEntity<SolicitudTraslado> finalizarSolicitud(@PathVariable Long numero) {
        try {
            SolicitudTraslado finalizada = solicitudTrasladoService.finalizarSolicitudTraslado(numero);
            return ResponseEntity.ok(finalizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}