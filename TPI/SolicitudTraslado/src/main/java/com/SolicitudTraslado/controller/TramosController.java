package com.SolicitudTraslado.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.SolicitudTraslado.services.TramoService;
import com.SolicitudTraslado.domain.Tramos;
import java.util.Map;

@RestController
@RequestMapping("/api/tramos")
public class TramosController {
    private final TramoService tramoService;

    public TramosController(TramoService tramosService) {
        this.tramoService = tramosService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tramos> obtenerTramoPorId(@PathVariable Long id) {
        Tramos tramo = tramoService.obtenerTramoPorId(id);
        return ResponseEntity.ok(tramo);
    }

    @GetMapping
    public ResponseEntity<Map<Long, Tramos>> listarTramos() {
        Map<Long, Tramos> tramos = tramoService.listarTramos();
        return ResponseEntity.ok(tramos);
    }

    @PostMapping
    public ResponseEntity<Tramos> crearTramo(@RequestBody Tramos tramo) {
        Tramos creado = tramoService.crearTramo(tramo);
        return ResponseEntity.ok(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tramos> actualizarTramo(@PathVariable Long id, @RequestBody Tramos tramo) {
        tramo.setId(id);
        Tramos actualizado = tramoService.actualizarTramo(tramo);
        return ResponseEntity.ok(actualizado);
    }
    
}
