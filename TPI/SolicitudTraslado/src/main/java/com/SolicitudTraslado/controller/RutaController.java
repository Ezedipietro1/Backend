package com.SolicitudTraslado.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.SolicitudTraslado.domain.Ruta;
import com.SolicitudTraslado.services.RutaService;
import java.util.Map;

@RestController
@RequestMapping("/api/rutas")
public class RutaController {
    private final RutaService rutaService;

    public RutaController(RutaService rutaService) {
        this.rutaService = rutaService;
    }

    @PostMapping
    public ResponseEntity<Ruta> crearRuta(@RequestBody Ruta ruta) {
        Ruta creado = rutaService.crearRuta(ruta);
        return ResponseEntity.ok(creado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ruta> obtenerRutaPorId(@PathVariable Long id) {
        Ruta ruta = rutaService.obtenerRutaPorId(id);
        if (ruta != null) {
            return ResponseEntity.ok(ruta);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ruta> actualizarRuta(@PathVariable Long id, @RequestBody Ruta ruta) {
        ruta.setId(id);
        Ruta actualizado = rutaService.actualizarRuta(ruta);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping
    public ResponseEntity<Map<Long, Ruta>> listarRutas() {
        Map<Long, Ruta> rutas = rutaService.listarRutas();
        return ResponseEntity.ok(rutas);
    }
    
}
