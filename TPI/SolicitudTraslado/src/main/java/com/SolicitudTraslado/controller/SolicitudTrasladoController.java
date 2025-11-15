package com.SolicitudTraslado.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.SolicitudTraslado.domain.Cliente;
import com.SolicitudTraslado.domain.Contenedor;
import com.SolicitudTraslado.domain.SolicitudTraslado;
import com.SolicitudTraslado.services.SolicitudTrasladoService;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudTrasladoController {

    private final SolicitudTrasladoService solicitudTrasladoService;

    public SolicitudTrasladoController(SolicitudTrasladoService solicitudTrasladoService) {
        this.solicitudTrasladoService = solicitudTrasladoService;
    }

    @PostMapping
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
}
