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
import org.springframework.web.bind.annotation.RestController;

import com.SolicitudTraslado.domain.Contenedor;
import com.SolicitudTraslado.services.ContenedorService;

@RestController
@RequestMapping("/api/contenedores")
public class ContenedorController {

    private final ContenedorService contenedorService;

    public ContenedorController(ContenedorService contenedorService) {
        this.contenedorService = contenedorService;
    }

    @GetMapping
    public ResponseEntity<Map<Long, Contenedor>> listarContenedores() {
        return ResponseEntity.ok(contenedorService.listarContenedores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contenedor> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(contenedorService.obtenerContenedorPorId(id));
    }

    @GetMapping("/en-deposito")
    public ResponseEntity<List<Contenedor>> obtenerContenedoresEnDeposito() {
        return ResponseEntity.ok(contenedorService.obtenerContenedoresEnDeposito());
    }

    @PostMapping
    public ResponseEntity<Contenedor> crearContenedor(@RequestBody Contenedor contenedor) {
        Contenedor creado = contenedorService.crearContenedor(contenedor);
        return ResponseEntity.ok(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contenedor> actualizarContenedor(@PathVariable Long id, @RequestBody Contenedor contenedor) {
        Contenedor actualizado = contenedorService.actualizarContenedor(contenedor, id);
        return ResponseEntity.ok(actualizado);
    }
}
