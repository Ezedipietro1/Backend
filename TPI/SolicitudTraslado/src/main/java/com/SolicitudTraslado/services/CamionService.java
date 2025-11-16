package com.SolicitudTraslado.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.SolicitudTraslado.config.RestTemplateFactory;
import com.SolicitudTraslado.domain.Camion;
import com.SolicitudTraslado.domain.Transportista;
import com.SolicitudTraslado.repo.CamionRepo;
import com.SolicitudTraslado.repo.TransportistaRepo;

@Service
public class CamionService {

    private final CamionRepo camionRepo;
    private final TransportistaRepo transportistaRepo;
    private final RestTemplateFactory restTemplateFactory;

    public CamionService(CamionRepo camionRepo, TransportistaRepo transportistaRepo, RestTemplateFactory restTemplateFactory) {
        this.camionRepo = camionRepo;
        this.transportistaRepo = transportistaRepo;
        this.restTemplateFactory = restTemplateFactory;
    }

    // servicio para que un operador / administrador cree un camion
    @Transactional
    public Camion crearCamion(Camion camion, String authHeader) {
        try {
            // RestTemplate con token para llamadas HTTP autenticadas a otros servicios
            // String token = authHeader.replace("Bearer ", "");
            // RestTemplate rt = restTemplateFactory.conToken(token);

        // validamos los datos del camion
        validarCamion(camion);

        // validamos si no existe otro camion con el mismo dominio
        if (camionRepo.existsById(camion.getDominio())) {
            throw new IllegalArgumentException("Ya existe un camión con el mismo dominio: " + camion.getDominio());
        }

        // obtenemos y validamos que el transportista exista --> tambien obtenemos su
        // dni para pasarlo directamente por postman
        String dniTransportista = null;
        if (camion.getTransportista() != null && camion.getTransportista().getDni() != null) {
            dniTransportista = camion.getTransportista().getDni();
        }

        if (dniTransportista == null || dniTransportista.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI del transportista es obligatorio");
        }

        // Validamos que el transportista exista y lo asignamos al camión
        Transportista transportista = transportistaRepo.findById(dniTransportista)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró un transportista con el DNI: "));
        camion.setTransportista(transportista);

        // Seteamos el estado por defecto si no viene
        if (camion.getEstado() == null) {
            camion.setEstado(true);
        }

        return camionRepo.save(camion);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al crear el camión: " + e.getMessage());
        }

    }

    // servicio para que un operador / administrador actualice un camion
    @Transactional
    public Camion actualizarCamion(Camion camion, String dominio) {
        Camion camionExistente = camionRepo.findById(dominio)
                .orElseThrow(() -> new IllegalArgumentException("Camión no encontrado con dominio: " + dominio));

        // actualizo los campos que pueden ser modificados
        validarCamion(camion);

        return camionRepo.save(camionExistente);

    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarCamiones() {
        List<Camion> camiones = camionRepo.findAll();
        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Camion camion : camiones) {
            Map<String, Object> camionData = new HashMap<>();
            camionData.put("dominio", camion.getDominio());
            camionData.put("capKg", camion.getCapKg());
            camionData.put("capVolumen", camion.getCapVolumen());
            camionData.put("consumo", camion.getConsumo());
            camionData.put("estado", camion.getEstado());
            camionData.put("transportistaDni", camion.getTransportista().getDni());
            resultado.add(camionData);
        }

        return resultado;
    }

    // analizar si es mejor devolver un map o el objeto camion directamente !!!!
    @Transactional(readOnly = true)
    public Camion obtenerCamionPorDominio(String dominio) {
        return camionRepo.findById(dominio)
                .orElseThrow(() -> new IllegalArgumentException("Camión no encontrado con dominio: " + dominio));
    }

    // ==================== MÉTODOS PRIVADOS DE VALIDACIÓN ====================

    private void validarCamion(Camion camion) {
        if (camion == null) {
            throw new IllegalArgumentException("El camión no puede ser null");
        }

        // Dominio: no nulo y no vacío
        if (camion.getDominio() == null || camion.getDominio().trim().isEmpty()) {
            throw new IllegalArgumentException("El dominio del camión es obligatorio");
        }

        // Dominio: formato válido (letras y números, típicamente 6-7 caracteres)
        String dominio = camion.getDominio().trim();
        if (!dominio.matches("^[A-Z]{2,3}\\d{3}[A-Z]{0,2}$") && !dominio.matches("^[A-Z]{3}\\d{3}$")) {
            throw new IllegalArgumentException("El dominio debe tener un formato válido (ej: ABC123 o AB123CD)");
        }

        // Capacidad en kg: debe ser positiva
        if (camion.getCapKg() == null || camion.getCapKg() <= 0) {
            throw new IllegalArgumentException("La capacidad de carga en kg debe ser un valor positivo");
        }

        // Capacidad en volumen: debe ser positiva
        if (camion.getCapVolumen() == null || camion.getCapVolumen() <= 0) {
            throw new IllegalArgumentException("La capacidad de volumen debe ser un valor positivo");
        }

        // Consumo: si viene, debe ser no negativo
        if (camion.getConsumo() != null && camion.getConsumo() < 0) {
            throw new IllegalArgumentException("El consumo no puede ser negativo");
        }

        // Transportista: debe existir
        if (camion.getTransportista() == null || camion.getTransportista().getDni() == null) {
            throw new IllegalArgumentException("El transportista es obligatorio");
        }
    }

}
