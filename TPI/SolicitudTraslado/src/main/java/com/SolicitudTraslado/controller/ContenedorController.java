package com.SolicitudTraslado.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/en_deposito")
    public ResponseEntity<List<Contenedor>> obtenerContenedoresEnDeposito() {
        return ResponseEntity.ok(contenedorService.obtenerContenedoresEnDeposito());
    }

    @PostMapping
    @PreAuthorize("hasRole('OPERADOR') or hasRole('CLIENTE')")
    public ResponseEntity<Contenedor> crearContenedor(@RequestBody Contenedor contenedor) {
        Contenedor creado = contenedorService.crearContenedor(contenedor);
        return ResponseEntity.ok(creado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Contenedor> actualizarContenedor(@PathVariable Long id, @RequestBody Contenedor contenedor) {
        Contenedor actualizado = contenedorService.actualizarContenedor(contenedor, id);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/no_entregados")
    public ResponseEntity<List<Contenedor>> obtenerContenedoresNoEntregados() {
        return ResponseEntity.ok(contenedorService.obtenerContenedoresNoEntregados());
    }
}
