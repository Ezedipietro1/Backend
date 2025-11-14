package com.SolicitudTraslado.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SolicitudTraslado.domain.Camion;
import com.SolicitudTraslado.services.CamionService;

@RestController
@RequestMapping("/api/camiones")
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
    public ResponseEntity<Camion> crearCamion(@RequestBody Camion camion) {
        Camion nuevoCamion = camionService.crearCamion(camion);
        return ResponseEntity.ok(nuevoCamion);
    }

    @PutMapping("/{dominio}")
    public ResponseEntity<Camion> actualizarCamion(@PathVariable String dominio, @RequestBody Camion camion) {
        Camion camionActualizado = camionService.actualizarCamion(camion, dominio);
        return ResponseEntity.ok(camionActualizado);
    }
}
