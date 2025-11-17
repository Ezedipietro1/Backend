package com.SolicitudTraslado.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.SolicitudTraslado.domain.Deposito;
import com.SolicitudTraslado.services.DepositoService;

@RestController
@RequestMapping("/api/depositos")
public class DepositoController {

    private final DepositoService depositoService;

    public DepositoController(DepositoService depositoService) {
        this.depositoService = depositoService;
    }

    @GetMapping
    public ResponseEntity<Map<Long, Deposito>> listarDepositos() {
        return ResponseEntity.ok(depositoService.listarDepositos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Deposito> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(depositoService.obtenerDepositoPorId(id));
    }

    @GetMapping("/por_ubicacion")
    public ResponseEntity<List<Deposito>> obtenerPorUbicacion(@RequestParam("ubicacionId") Long ubicacionId) {
        return ResponseEntity.ok(depositoService.obtenerDepositosPorUbicacionId(ubicacionId));
    }

    @GetMapping("/por_ciudad")
    public ResponseEntity<List<Deposito>> obtenerPorCiudad(@RequestParam("ciudadId") Long ciudadId) {
        return ResponseEntity.ok(depositoService.obtenerDepositosPorCiudadId(ciudadId));
    }

    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Deposito> crearDeposito(@RequestBody Deposito deposito) {
        Deposito creado = depositoService.crearDeposito(deposito);
        return ResponseEntity.ok(creado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Deposito> actualizarDeposito(@PathVariable Long id, @RequestBody Deposito deposito) {
        // Aseguramos que el ID del path se use en el depósito
        deposito.setId(id);
        Deposito actualizado = depositoService.actualizarDeposito(deposito);
        return ResponseEntity.ok(actualizado);
    }
}
