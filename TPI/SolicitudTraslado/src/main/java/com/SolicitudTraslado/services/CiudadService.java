package com.SolicitudTraslado.services;

import com.SolicitudTraslado.domain.Ciudad;
import com.SolicitudTraslado.repo.CiudadRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class CiudadService {
    private final CiudadRepo ciudadRepo;

    public CiudadService(CiudadRepo ciudadRepo) {
        this.ciudadRepo = ciudadRepo;
    }

    @Transactional(readOnly = true)
    public Ciudad obtenerCiudadPorId(Long id) {
        return ciudadRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ciudad no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    public Ciudad obtenerCiudadPorNombre(String nombre) {
        return ciudadRepo.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Ciudad no encontrada con nombre: " + nombre));
    }
    
    @Transactional
    public Ciudad crearCiudad(Ciudad ciudad) {
        validarCiudad(ciudad);
        return ciudadRepo.save(ciudad);
    }

    @Transactional
    public Ciudad actualizarCiudad(Ciudad ciudadActualizada) {  
        validarCiudad(ciudadActualizada);
        return ciudadRepo.save(ciudadActualizada);
    }

    @Transactional(readOnly = true)
    public java.util.List<Ciudad> listarCiudades() {
        return ciudadRepo.findAll();
    }

    // Validación para Ciudad (similar al ejemplo del cliente)
    private void validarCiudad(Ciudad c) {
        if (c == null) {
            throw new IllegalArgumentException("Ciudad no puede ser null");
        }

        // Nombre: obligatorio y no vacío
        if (Objects.isNull(c.getNombre()) || c.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la ciudad es obligatorio");
        }

        String nombre = c.getNombre().trim();
        // No debe contener dígitos
        if (Pattern.matches(".*\\d.*", nombre)) {
            throw new IllegalArgumentException("El nombre de la ciudad no puede contener números");
        }

        // Longitud máxima razonable
        if (nombre.length() > 100) {
            throw new IllegalArgumentException("El nombre de la ciudad es demasiado largo");
        }
    }
}
