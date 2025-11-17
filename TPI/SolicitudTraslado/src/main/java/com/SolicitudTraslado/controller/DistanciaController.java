package com.SolicitudTraslado.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.SolicitudTraslado.services.DistanciaService;

@RestController
@RequestMapping("/api/distancias")
public class DistanciaController {

    private final DistanciaService distanciaService;
    private final Logger log = LoggerFactory.getLogger(DistanciaController.class);

    public DistanciaController(DistanciaService distanciaService) {
        this.distanciaService = distanciaService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> calcularDistancia(
            @RequestParam Double origenLat,
            @RequestParam Double origenLng,
            @RequestParam Double destinoLat,
            @RequestParam Double destinoLng) {

        Map<String, Object> result = distanciaService.calcularDistancia(origenLat, origenLng, destinoLat, destinoLng);
        if (result == null) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno"));
        }
        if (result.containsKey("error")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> calcularDistanciaPost(@RequestBody DistanciaRequest body) {
        if (body == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Request body vacío"));
        }

        Map<String, Object> result = distanciaService.calcularDistancia(
                body.origenLat(), body.origenLng(), body.destinoLat(), body.destinoLng());

        if (result == null) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno"));
        }
        if (result.containsKey("error")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    public record DistanciaRequest(Double origenLat, Double origenLng, Double destinoLat, Double destinoLng) {
    }
}
