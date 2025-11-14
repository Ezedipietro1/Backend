package com.SolicitudTraslado.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SolicitudTraslado.domain.Contenedor;
import com.SolicitudTraslado.domain.enums.EstadoContenedor;
import com.SolicitudTraslado.repo.ContenedorRepo;

@Service
public class ContenedorService {

    private final ContenedorRepo contenedorRepo;

    public ContenedorService(ContenedorRepo contenedorRepo) {
        this.contenedorRepo = contenedorRepo;
    }

    // servicio para crear un contenedor
    @Transactional
    public Contenedor crearContenedor(Contenedor contenedor) {
        // validamos que no exista otro contenedor con el mismo id
        if (contenedor.getId() != null && contenedorRepo.existsById(contenedor.getId())) {
            throw new IllegalArgumentException("Ya existe un contenedor con el mismo id: " + contenedor.getId());
        }

        // validamos los datos del contenedor
        validarContenedor(contenedor);

        return contenedorRepo.save(contenedor);
    }

    // servicio para actualizar un contenedor
    @Transactional
    public Contenedor actualizarContenedor(Contenedor contenedor, Long id) {
        Contenedor contenedorExistente = contenedorRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contenedor no encontrado con id: " + id));

        // validamos los datos del contenedor
        validarContenedor(contenedor);

        return contenedorRepo.save(contenedorExistente);
    }

    // servicio para listar todos los contenedores en un mapa con su id como clave
    @Transactional(readOnly = true)
    public Map<Long, Contenedor> listarContenedores() {
        List<Contenedor> contenedores = contenedorRepo.findAll();
        Map<Long, Contenedor> contenedorMap = new HashMap<>();
        for (Contenedor contenedor : contenedores) {
            contenedorMap.put(contenedor.getId(), contenedor);
        }
        return contenedorMap;
    }

    @Transactional(readOnly = true)
    public Contenedor obtenerContenedorPorId(Long id) {
        return contenedorRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contenedor no encontrado con id: " + id));
    }

    // servicio para obtener una lista de contenedores que esten en el deposito
    @Transactional(readOnly = true)
    public List<Contenedor> obtenerContenedoresEnDeposito() {
        return contenedorRepo.findByEstadoContenedor(EstadoContenedor.EN_DEPOSITO);
    }

    // ==================== MÉTODOS PRIVADOS DE VALIDACIÓN ====================

    private void validarContenedor(Contenedor contenedor) {
        if (contenedor == null) {
            throw new IllegalArgumentException("El contenedor no puede ser null");
        }

        // Volumen: debe ser positivo
        if (contenedor.getVolumen() == null || contenedor.getVolumen() <= 0) {
            throw new IllegalArgumentException("El volumen debe ser un valor positivo");
        }

        // Peso: debe ser positivo
        if (contenedor.getPeso() == null || contenedor.getPeso() <= 0) {
            throw new IllegalArgumentException("El peso debe ser un valor positivo");
        }

        // Estado: si viene, debe ser un valor válido del enum
        if (contenedor.getEstadoContenedor() != null) {
            validarEstadoContenedor(contenedor.getEstadoContenedor());
        }
    }

    private void validarEstadoContenedor(EstadoContenedor estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado del contenedor no puede ser null");
        }

        // Verificar que el estado sea uno de los valores válidos del enum
        boolean esValido = false;
        for (EstadoContenedor estadoValido : EstadoContenedor.values()) {
            if (estadoValido == estado) {
                esValido = true;
                break;
            }
        }

        if (!esValido) {
            throw new IllegalArgumentException(
                    "Estado de contenedor inválido. Estados permitidos: EN_ESPERA_RETIRO, RETIRADO, EN_VIAJE, EN_DEPOSITO, ENTREGADO");
        }
    }

}
