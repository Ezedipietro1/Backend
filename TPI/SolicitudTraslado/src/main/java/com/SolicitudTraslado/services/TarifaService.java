package com.SolicitudTraslado.services;

import com.SolicitudTraslado.domain.Tarifa;
import com.SolicitudTraslado.repo.TarifaRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;


@Service
public class TarifaService {
    private final TarifaRepo tarifaRepo;
    
    public TarifaService(TarifaRepo tarifaRepo) {
        this.tarifaRepo = tarifaRepo;
    }

    @Transactional(readOnly = true)
    public Tarifa obtenerTarifaPorId(Long id) {
        return tarifaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarifa no encontrada con ID: " + id));
    }

    @Transactional
    public Tarifa crearTarifa(Tarifa tarifa) {
        validarTarifa(tarifa);
        return tarifaRepo.save(tarifa);
    }

    @Transactional
    public Tarifa actualizarTarifa(Tarifa tarifaActualizada) {
        validarTarifa(tarifaActualizada);
        return tarifaRepo.save(tarifaActualizada);
    }

    @Transactional(readOnly = true)
    public HashMap<Long, Tarifa> listarTarifas() {
        HashMap<Long, Tarifa> tarifaMap = new HashMap<>();
        for (Tarifa tarifa : tarifaRepo.findAll()) {
            tarifaMap.put(tarifa.getId(), tarifa);
        }
        return tarifaMap;
    }

    // Validación para Tarifa
    private void validarTarifa(Tarifa t) {
        if (t == null) {
            throw new IllegalArgumentException("Tarifa no puede ser null");
        }

        if (t.getCostoPorKm() == null || t.getCostoPorKm() <= 0) {
            throw new IllegalArgumentException("El costo por km debe ser un valor positivo");
        }

        if (t.getCostoDeCombustible() == null || t.getCostoDeCombustible() < 0) {
            throw new IllegalArgumentException("El costo de combustible debe ser 0 o un valor positivo");
        }

        if (t.getCostoPorM3() == null || t.getCostoPorM3() < 0) {
            throw new IllegalArgumentException("El costo por m3 debe ser 0 o un valor positivo");
        }
    }
}
