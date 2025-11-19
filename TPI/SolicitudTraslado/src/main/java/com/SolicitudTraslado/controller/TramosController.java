package com.SolicitudTraslado.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.SolicitudTraslado.services.TramoService;
import com.SolicitudTraslado.dto.TramoDTO;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/solicitudes/tramos")
public class TramosController {
    private final TramoService tramoService;

    public TramosController(TramoService tramosService) {
        this.tramoService = tramosService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TramoDTO> obtenerTramoPorId(@PathVariable Long id) {
        TramoDTO tramo = tramoService.obtenerTramoDtoPorId(id);
        return ResponseEntity.ok(tramo);
    }

    @GetMapping
    public ResponseEntity<Map<Long, TramoDTO>> listarTramos() {
        Map<Long, TramoDTO> tramos = tramoService.listarTramosDto();
        return ResponseEntity.ok(tramos);
    }

    @PostMapping
    public ResponseEntity<TramoDTO> crearTramo(@RequestBody TramoDTO tramo) {
        TramoDTO creado = tramoService.crearTramo(tramo);
        return ResponseEntity.ok(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TramoDTO> actualizarTramo(@PathVariable Long id, @RequestBody TramoDTO tramo) {
        tramo.setId(id);
        TramoDTO actualizado = tramoService.actualizarTramo(tramo);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/{dominio}")
    @PreAuthorize("hasRole('ROLE_TRANSPORTISTA')")
    public ResponseEntity<List<TramoDTO>> obtenerTramoPorDominio(@PathVariable String dominio) {
        List<TramoDTO> tramo = tramoService.obtenerTramosDtoPorCamionDominio(dominio);
        return ResponseEntity.ok(tramo);
    }

    @PutMapping("/finalizar/{id}")
    @PreAuthorize("hasRole('ROLE_TRANSPORTISTA')")
    public ResponseEntity<TramoDTO> finalizarTramo(@PathVariable Long id) {
        TramoDTO finalizado = tramoService.finalizarTramoDto(id);
        return ResponseEntity.ok(finalizado);
    }

    @PutMapping("/iniciar/{id}")
    @PreAuthorize("hasRole('ROLE_TRANSPORTISTA')")
    public ResponseEntity<TramoDTO> iniciarTramo(@PathVariable Long id) {
        TramoDTO iniciado = tramoService.iniciaTramosDto(id);
        return ResponseEntity.ok(iniciado);
    }

}
