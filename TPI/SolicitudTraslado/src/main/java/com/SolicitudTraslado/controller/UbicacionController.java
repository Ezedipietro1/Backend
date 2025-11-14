package com.SolicitudTraslado.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SolicitudTraslado.domain.Ubicacion;
import com.SolicitudTraslado.services.UbicacionService;

@RestController
@RequestMapping("/api/ubicaciones")
public class UbicacionController {

    private final UbicacionService ubicacionService;

    public UbicacionController(UbicacionService ubicacionService) {
        this.ubicacionService = ubicacionService;
    }

    @GetMapping
    public ResponseEntity<Map<Long, Ubicacion>> listarUbicaciones() {
        return ResponseEntity.ok(ubicacionService.listarUbicaciones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ubicacion> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ubicacionService.obtenerUbicacionPorId(id));
    }

    @GetMapping("/por-ciudad")
    public ResponseEntity<List<Ubicacion>> obtenerPorCiudad(@RequestParam("ciudadId") Long ciudadId) {
        return ResponseEntity.ok(ubicacionService.obtenerUbicacionesPorCiudadId(ciudadId));
    }

    @PostMapping
    public ResponseEntity<Ubicacion> crearUbicacion(@RequestBody Ubicacion ubicacion) {
        Ubicacion creada = ubicacionService.crearUbicacion(ubicacion);
        return ResponseEntity.ok(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ubicacion> actualizarUbicacion(@PathVariable Long id, @RequestBody Ubicacion ubicacion) {
        // Aseguramos que el ID del path se use en la ubicación
        ubicacion.setId(id);
        Ubicacion actualizada = ubicacionService.actualizarUbicacion(ubicacion);
        return ResponseEntity.ok(actualizada);
    }
}
