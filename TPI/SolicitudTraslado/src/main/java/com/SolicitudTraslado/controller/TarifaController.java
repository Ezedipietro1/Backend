package com.SolicitudTraslado.controller;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.SolicitudTraslado.domain.Tarifa;
import com.SolicitudTraslado.services.TarifaService;

@RestController
@RequestMapping("/api/tarifas")
public class TarifaController {

    private final TarifaService tarifaService;

    public TarifaController(TarifaService tarifaService) {
        this.tarifaService = tarifaService;
    }

    @GetMapping
    public ResponseEntity<HashMap<Long, Tarifa>> listarTarifas() {
        return ResponseEntity.ok(tarifaService.listarTarifas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarifa> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tarifaService.obtenerTarifaPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Tarifa> crearTarifa(@RequestBody Tarifa tarifa) {
        Tarifa creada = tarifaService.crearTarifa(tarifa);
        return ResponseEntity.ok(creada);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Tarifa> actualizarTarifa(@PathVariable Long id, @RequestBody Tarifa tarifa) {
        // Aseguramos que el ID del path se use en la tarifa
        tarifa.setId(id);
        Tarifa actualizada = tarifaService.actualizarTarifa(tarifa);
        return ResponseEntity.ok(actualizada);
    }

    // Endpoints para la tarifa única (actual)
    @GetMapping("/current")
    public ResponseEntity<Tarifa> getTarifaActual() {
        Tarifa t = tarifaService.obtenerTarifa();
        return ResponseEntity.ok(t);
    }

    @PutMapping("/current")
    public ResponseEntity<Tarifa> updateTarifaActual(@RequestBody Tarifa tarifa) {
        Tarifa actualizado = tarifaService.actualizarTarifa(tarifa);
        return ResponseEntity.ok(actualizado);
    }
}
