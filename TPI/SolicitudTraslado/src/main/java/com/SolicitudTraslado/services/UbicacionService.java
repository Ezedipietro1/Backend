package com.SolicitudTraslado.services;

import com.SolicitudTraslado.domain.Ciudad;
import com.SolicitudTraslado.domain.Ubicacion;
import com.SolicitudTraslado.repo.UbicacionRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;


@Service
public class UbicacionService {
    private final UbicacionRepo ubicacionRepo;

    public UbicacionService(UbicacionRepo ubicacionRepo) {
        this.ubicacionRepo = ubicacionRepo;
    }

    @Transactional(readOnly = true)
    public Ubicacion obtenerUbicacionPorId(Long id) {
        return ubicacionRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ubicación no encontrada con ID: " + id));
    }

    @Transactional
    public Ubicacion crearUbicacion(Ubicacion ubicacion) {
        validarUbicacion(ubicacion);
        return ubicacionRepo.save(ubicacion);
    }

    @Transactional
    public Ubicacion actualizarUbicacion(Ubicacion ubicacionActualizada) {
        validarUbicacion(ubicacionActualizada);
        return ubicacionRepo.save(ubicacionActualizada);
    }

    @Transactional(readOnly = true)
    public Map<Long, Ubicacion> listarUbicaciones() {
        List<Ubicacion> ubicaciones = ubicacionRepo.findAll();
        Map<Long, Ubicacion> ubicacionMap = new HashMap<>();
        for (Ubicacion ub : ubicaciones) {
            ubicacionMap.put(ub.getId(), ub);
        }
        return ubicacionMap;
    }

    @Transactional(readOnly = true)
    public List<Ubicacion> obtenerUbicacionesPorCiudadId(Long ciudadId) {
        return ubicacionRepo.findByCiudadId(ciudadId);
    }

    @Transactional(readOnly = true)
    public List<Ubicacion> obtenerUbicacionesPorCiudad(Ciudad ciudad) {
        return ubicacionRepo.findByCiudad(ciudad);
    }

    // Validación para Ubicacion
    private void validarUbicacion(Ubicacion u) {
        if (u == null) {
            throw new IllegalArgumentException("Ubicación no puede ser null");
        }

        if (Objects.isNull(u.getDireccion()) || u.getDireccion().trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección es obligatoria");
        }

        if (u.getLatitud() == null || u.getLongitud() == null) {
            throw new IllegalArgumentException("Latitud y longitud son obligatorias");
        }
        double lat = u.getLatitud();
        double lon = u.getLongitud();
        if (lat < -90.0 || lat > 90.0) {
            throw new IllegalArgumentException("Latitud fuera de rango (-90..90)");
        }
        if (lon < -180.0 || lon > 180.0) {
            throw new IllegalArgumentException("Longitud fuera de rango (-180..180)");
        }

        if (u.getCiudad() == null) {
            throw new IllegalArgumentException("La ciudad de la ubicación es obligatoria");
        }

        if (u.getCiudad().getId() == null) {
            // si no tiene id, exigir nombre válido
            if (Objects.isNull(u.getCiudad().getNombre()) || u.getCiudad().getNombre().trim().isEmpty()) {
                throw new IllegalArgumentException("La ciudad debe tener id o nombre");
            }
            if (Pattern.matches(".*\\d.*", u.getCiudad().getNombre())) {
                throw new IllegalArgumentException("El nombre de la ciudad no puede contener números");
            }
        }
    }
}
