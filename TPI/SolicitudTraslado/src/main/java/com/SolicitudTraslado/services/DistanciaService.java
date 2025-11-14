package com.SolicitudTraslado.services;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servicio de distancia adaptado al proyecto.
 *
 * - Usa GoogleMapsService cuando está configurado y devuelve la distancia en
 * km.
 * - Si Google Maps no está configurado o falla, usa Haversine como fallback.
 */
@Service
public class DistanciaService {

    private final GoogleMapsService googleMapsService;
    private final Logger log = LoggerFactory.getLogger(DistanciaService.class);

    public DistanciaService(GoogleMapsService googleMapsService) {
        this.googleMapsService = googleMapsService;
    }

    /**
     * Calcula la distancia entre dos puntos y devuelve un mapa con información.
     * Retorna un Map con claves: "distanceKm" (Double), "unit" ("km"), "method"
     * ("google"|"haversine").
     * En caso de coordenadas inválidas, devuelve un mapa con clave "error".
     */
    public Map<String, Object> calcularDistancia(Double origenLat, Double origenLng,
            Double destinoLat, Double destinoLng) {
        Map<String, Object> result = new HashMap<>();

        if (origenLat == null || origenLng == null || destinoLat == null || destinoLng == null) {
            result.put("error", "Coordenadas inválidas");
            return result;
        }

        Double distanceKm = null;
        String method = "haversine";

        // Intentar Google Maps si está configurado
        try {
            if (googleMapsService != null && googleMapsService.isConfigured()) {
                Double gmDistance = googleMapsService.calcularDistancia(origenLat, origenLng, destinoLat, destinoLng);
                if (gmDistance != null) {
                    distanceKm = gmDistance;
                    method = "google";
                }
            }
        } catch (Exception e) {
            log.warn("GoogleMapsService falló, usando Haversine como fallback", e);
        }

        // Si no se obtuvo distancia de Google, usar Haversine
        if (distanceKm == null) {
            distanceKm = calcularDistanciaHaversine(origenLat, origenLng, destinoLat, destinoLng);
            method = "haversine";
        }

        result.put("distanceKm", distanceKm);
        result.put("unit", "km");
        result.put("method", method);
        return result;
    }

    /**
     * Cálculo Haversine (duplicado localmente para no depender de métodos privados
     * en GoogleMapsService)
     */
    private Double calcularDistanciaHaversine(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int RADIO_TIERRA_KM = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = RADIO_TIERRA_KM * c;
        return distance;
    }

}