package com.SolicitudTraslado.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Servicio para calcular distancias usando Google Maps Directions API
 * con fallback a cálculo Haversine
 */
@Service
public class GoogleMapsService {

    private final RestTemplate restTemplate;

    @Value("${google.maps.api.key:}")
    private String apiKey;

    @Value("${google.maps.api.url:https://maps.googleapis.com/maps/api/directions/json}")
    private String apiUrl;

    public GoogleMapsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Calcula la distancia en kilómetros entre dos puntos geográficos
     * 
     * @param origenLat  Latitud del origen
     * @param origenLng  Longitud del origen
     * @param destinoLat Latitud del destino
     * @param destinoLng Longitud del destino
     * @return Distancia en kilómetros, o null si hay error
     */
    public Double calcularDistancia(Double origenLat, Double origenLng,
            Double destinoLat, Double destinoLng) {

        // Si no hay API key configurada, usar cálculo de distancia haversine
        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("[GoogleMapsService] API key no configurada, usando cálculo Haversine");
            return calcularDistanciaHaversine(origenLat, origenLng, destinoLat, destinoLng);
        }

        // Validar coordenadas
        if (origenLat == null || origenLng == null || destinoLat == null || destinoLng == null) {
            System.err.println("[GoogleMapsService] Coordenadas nulas recibidas");
            return null;
        }

        try {
            String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .queryParam("origin", origenLat + "," + origenLng)
                    .queryParam("destination", destinoLat + "," + destinoLng)
                    .queryParam("key", apiKey)
                    .queryParam("mode", "driving")
                    .toUriString();

            System.out.println("[GoogleMapsService] Consultando Google Maps API...");

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null) {
                Object statusObj = response.get("status");
                String status = statusObj != null ? statusObj.toString() : null;
                if ("OK".equals(status)) {
                    Object routesObj = response.get("routes");
                    if (routesObj instanceof java.util.List<?> routesList && !routesList.isEmpty()) {
                        Object firstRoute = routesList.get(0);
                        if (firstRoute instanceof java.util.Map<?, ?> routeMap) {
                            Object legsObj = routeMap.get("legs");
                            if (legsObj instanceof java.util.List<?> legsList && !legsList.isEmpty()) {
                                Object firstLeg = legsList.get(0);
                                if (firstLeg instanceof java.util.Map<?, ?> legMap) {
                                    Object distanceObj = legMap.get("distance");
                                    if (distanceObj instanceof java.util.Map<?, ?> distanceMap) {
                                        Object valueObj = distanceMap.get("value");
                                        if (valueObj instanceof Number num) {
                                            double distanceKm = num.doubleValue() / 1000.0;
                                            System.out.println(
                                                    "[GoogleMapsService] Distancia calculada: " + distanceKm + " km");
                                            return distanceKm;
                                        } else if (valueObj != null) {
                                            try {
                                                double parsed = Double.parseDouble(valueObj.toString());
                                                double distanceKm = parsed / 1000.0;
                                                System.out.println("[GoogleMapsService] Distancia calculada (parsed): "
                                                        + distanceKm + " km");
                                                return distanceKm;
                                            } catch (NumberFormatException ignored) {
                                                // fallthrough to haversine
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    System.err.println(
                            "[GoogleMapsService] Google Maps status: " + status + ", full response: " + response);
                }
            } else {
                System.err.println("[GoogleMapsService] Respuesta nula de Google Maps");
            }

            // Si algo falla en el parsing de la respuesta, usar fallback Haversine
            return calcularDistanciaHaversine(origenLat, origenLng, destinoLat, destinoLng);

        } catch (Exception e) {
            System.err.println("[GoogleMapsService] Error al consultar Google Maps: " + e.getMessage());
            return calcularDistanciaHaversine(origenLat, origenLng, destinoLat, destinoLng);
        }
    }

    /**
     * Calcula la distancia en línea recta usando la fórmula de Haversine
     * Usado como fallback cuando Google Maps no está disponible
     */
    private Double calcularDistanciaHaversine(Double lat1, Double lon1,
            Double lat2, Double lon2) {
        final int RADIO_TIERRA_KM = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = RADIO_TIERRA_KM * c;

        System.out.println("[GoogleMapsService] Distancia Haversine: " +
                String.format("%.2f", distance) + " km");

        return distance;
    }

    /**
     * Valida si el servicio de Google Maps está configurado
     */
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isEmpty();
    }
}