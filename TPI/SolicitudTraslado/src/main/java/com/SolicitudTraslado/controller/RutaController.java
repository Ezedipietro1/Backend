package com.SolicitudTraslado.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.SolicitudTraslado.domain.Ruta;
import com.SolicitudTraslado.services.RutaService;
import com.SolicitudTraslado.domain.SolicitudTraslado;
import com.SolicitudTraslado.services.SolicitudTrasladoService;
import java.util.Map;
import java.util.HashMap; 
import java.util.List;

@RestController
@RequestMapping("/api/rutas")
public class RutaController {
    private final RutaService rutaService;
    private final SolicitudTrasladoService solicitudTrasladoService;

    public RutaController(RutaService rutaService, SolicitudTrasladoService solicitudTrasladoService) {
        this.rutaService = rutaService;
        this.solicitudTrasladoService = solicitudTrasladoService;
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

    @GetMapping("/para_asignar")
    public ResponseEntity<Map<Long, Ruta>> obtenerRutasParaAsignarASolicitud(@RequestParam Long solicitudId) {
        // Aquí se asume que existe un método para obtener la solicitud por ID
        SolicitudTraslado solicitud = solicitudTrasladoService.obtenerSolicitudPorNumero(solicitudId);
        if (solicitud == null) {
            return ResponseEntity.notFound().build();
        }
        List<Ruta> rutas = rutaService.obtenerRutasParaAsignarASolicitud(solicitud);
        Map<Long, Ruta> rutaMap = new HashMap<>();
        for (Ruta ruta : rutas) {
            rutaMap.put(ruta.getId(), ruta);
        }
        return ResponseEntity.ok(rutaMap);
    }
    
}
