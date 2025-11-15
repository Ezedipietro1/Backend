package com.SolicitudTraslado.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.SolicitudTraslado.domain.Transportista;
import com.SolicitudTraslado.services.TransportistaService;

@RestController
@RequestMapping("/api/transportistas")
public class TransportistaController {

    private final TransportistaService service;

    public TransportistaController(TransportistaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Transportista> crearTransportista(@RequestBody Transportista t) {
        return ResponseEntity.ok(service.crearTransportista(t));
    }

    @GetMapping
    public ResponseEntity<List<Transportista>> listarTransportistas() {
        return ResponseEntity.ok(service.listarTransportistas());
    }

    @GetMapping("/{dni}")
    public ResponseEntity<Transportista> obtenerTransportistaPorDni(@PathVariable String dni) {
        return ResponseEntity.ok(service.obtenerTransportistaPorDni(dni));
    }
}