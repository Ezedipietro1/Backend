package com.SolicitudTraslado.services;

import com.SolicitudTraslado.domain.Ruta;
import com.SolicitudTraslado.repo.RutaRepo;
import com.SolicitudTraslado.repo.UbicacionRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class RutaService {
    private final RutaRepo rutaRepo;
    private final UbicacionRepo ubicacionRepo;

    public RutaService(RutaRepo rutaRepo, UbicacionRepo ubicacionRepo) {
        this.rutaRepo = rutaRepo;
        this.ubicacionRepo = ubicacionRepo;
    }

    @Transactional
    public Ruta crearRuta(Ruta ruta) {
        validarRuta(ruta);
        return rutaRepo.save(ruta);
    }

    @Transactional
    public Ruta actualizarRuta(Ruta rutaActualizada) {
        validarRuta(rutaActualizada);
        return rutaRepo.save(rutaActualizada);
    }

    @Transactional(readOnly = true)
    public Ruta obtenerRutaPorId(Long id) {
        return rutaRepo.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Ruta> obtenerRutasPorAsignada(Boolean asignada) {
        if (asignada == null) {
            throw new IllegalArgumentException("El parámetro 'asignada' no puede ser null");
        }
        return rutaRepo.findByAsignada(asignada);
    }

    @Transactional(readOnly = true)
    public Map<Long, Ruta> listarRutas() {
        Map<Long, Ruta> rutaMap = new HashMap<>();
        for (Ruta ruta : rutaRepo.findAll()) {
            rutaMap.put(ruta.getId(), ruta);
        }
        return rutaMap;
    }

    @Transactional(readOnly = true)
    public List<Ruta> obtenerRutasPorSolicitud(Long numeroSolicitud) {
        if (numeroSolicitud == null) {
            throw new IllegalArgumentException("El número de solicitud no puede ser null");
        }
        return rutaRepo.findBySolicitudNumero(numeroSolicitud);
    }

    @Transactional(readOnly = true)
    public List<Ruta> obtenerRutasPorOrigenYDestino(Long origenId, Long destinoId) {
        if (origenId == null || destinoId == null) {
            throw new IllegalArgumentException("Los IDs de origen y destino no pueden ser null");
        }
        return rutaRepo.findByOrigenIdAndDestinoId(origenId, destinoId);
    }

    
    // Validaciones para Ruta
    private void validarRuta(Ruta r) {
        if (r == null) {
            throw new IllegalArgumentException("Ruta no puede ser null");
        }

        // Origen y destino obligatorios y deben existir
        if (r.getOrigen() == null || r.getOrigen().getId() == null) {
            throw new IllegalArgumentException("El origen de la ruta es obligatorio");
        }
        if (r.getDestino() == null || r.getDestino().getId() == null) {
            throw new IllegalArgumentException("El destino de la ruta es obligatorio");
        }

        Long origenId = r.getOrigen().getId();
        Long destinoId = r.getDestino().getId();

        ubicacionRepo.findById(origenId).orElseThrow(() -> new IllegalArgumentException("Ubicación origen no encontrada"));
        ubicacionRepo.findById(destinoId).orElseThrow(() -> new IllegalArgumentException("Ubicación destino no encontrada"));

        if (Objects.equals(origenId, destinoId)) {
            throw new IllegalArgumentException("Origen y destino no pueden ser la misma ubicación");
        }

        // Distancia: obligatoria y positiva
        if (r.getDistancia() == null || r.getDistancia() <= 0) {
            throw new IllegalArgumentException("La distancia debe ser un valor positivo");
        }

        // Cantidad de tramos: obligatoria y positiva
        if (r.getCantTramos() == null || r.getCantTramos() <= 0) {
            throw new IllegalArgumentException("La cantidad de tramos debe ser un valor mayor a 0");
        }

        // Solicitud: verificar que tenga id (la entidad puede pertenecer a otro microservicio)
        if (r.getSolicitud() == null || r.getSolicitud().getNumero() == null) {
            throw new IllegalArgumentException("La solicitud asociada a la ruta es obligatoria");
        }

        // Asignada: no debe ser null
        if (r.getAsignada() == null) {
            throw new IllegalArgumentException("El campo 'asignada' es obligatorio");
        }

        // Evitar duplicados: si existe otra ruta con mismo origen y destino, y no es la misma ruta, impedirla
        java.util.List<Ruta> existentes = rutaRepo.findByOrigenIdAndDestinoId(origenId, destinoId);
        if (existentes != null && !existentes.isEmpty()) {
            boolean otro = existentes.stream().anyMatch(rt -> r.getId() == null || !rt.getId().equals(r.getId()));
            if (otro) {
                throw new IllegalArgumentException("Ya existe una ruta con el mismo origen y destino");
            }
        }
    }
    
}
