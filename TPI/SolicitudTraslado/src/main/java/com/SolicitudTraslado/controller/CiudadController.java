package com.SolicitudTraslado.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SolicitudTraslado.domain.Ciudad;
import com.SolicitudTraslado.services.CiudadService;

@RestController
@RequestMapping("/api/ciudades")
public class CiudadController {

    private final CiudadService ciudadService;

    public CiudadController(CiudadService ciudadService) {
        this.ciudadService = ciudadService;
    }

    @GetMapping
    public ResponseEntity<List<Ciudad>> listarCiudades() {
        return ResponseEntity.ok(ciudadService.listarCiudades());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ciudad> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ciudadService.obtenerCiudadPorId(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Ciudad> buscarPorNombre(@RequestParam("nombre") String nombre) {
        return ResponseEntity.ok(ciudadService.obtenerCiudadPorNombre(nombre));
    }

    @PostMapping
    public ResponseEntity<Ciudad> crearCiudad(@RequestBody Ciudad ciudad) {
        Ciudad creada = ciudadService.crearCiudad(ciudad);
        return ResponseEntity.ok(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ciudad> actualizarCiudad(@PathVariable Long id, @RequestBody Ciudad ciudad) {
        // Aseguramos que el ID del path se use en la ciudad
        ciudad.setId(id);
        Ciudad actualizada = ciudadService.actualizarCiudad(ciudad);
        return ResponseEntity.ok(actualizada);
    }
}
