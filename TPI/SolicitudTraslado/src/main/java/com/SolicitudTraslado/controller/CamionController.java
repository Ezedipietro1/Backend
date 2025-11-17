package com.SolicitudTraslado.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.SolicitudTraslado.domain.Camion;
import com.SolicitudTraslado.services.CamionService;

@RestController
@RequestMapping("/api/solicitudes/camiones")
public class CamionController {
    private final CamionService camionService;

    public CamionController(CamionService camionService) {
        this.camionService = camionService;
    }

    @GetMapping("/{dominio}")
    public ResponseEntity<Camion> obtenerCamionPorId(@PathVariable String dominio) {
        Camion camion = camionService.obtenerCamionPorDominio(dominio);
        return ResponseEntity.ok(camion);
    }

    @GetMapping
    public ResponseEntity<?> listarCamiones() {
        return ResponseEntity.ok(camionService.listarCamiones());
    }

    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Camion> crearCamion(@RequestBody Camion camion) {
        Camion nuevoCamion = camionService.crearCamion(camion);
        return ResponseEntity.ok(nuevoCamion);
    }

    @PutMapping("/{dominio}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Camion> actualizarCamion(@PathVariable String dominio, @RequestBody Camion camion) {
        Camion camionActualizado = camionService.actualizarCamion(camion, dominio);
        return ResponseEntity.ok(camionActualizado);
    }
}
