package com.SolicitudTraslado.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Transportista> crear(@RequestBody Transportista t) {
        return ResponseEntity.ok(service.crearTransportista(t));
    }

    @GetMapping
    public ResponseEntity<List<Transportista>> listar() {
        return ResponseEntity.ok(service.listarTransportistas());
    }

    @GetMapping("/{dni}")
    public ResponseEntity<Transportista> porDni(@PathVariable String dni) {
        return ResponseEntity.ok(service.obtenerTransportistaPorDni(dni));
    }
}