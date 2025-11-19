package com.SolicitudTraslado.services;

import com.SolicitudTraslado.domain.Ruta;
import com.SolicitudTraslado.repo.RutaRepo;
import com.SolicitudTraslado.repo.UbicacionRepo;
import com.SolicitudTraslado.domain.SolicitudTraslado;
import com.SolicitudTraslado.domain.Ubicacion;
import com.SolicitudTraslado.dto.DtoMapper;
import com.SolicitudTraslado.dto.RutaDTO;
import com.SolicitudTraslado.services.UbicacionService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class RutaService {
    private final RutaRepo rutaRepo;
    private final UbicacionRepo ubicacionRepo;
    private final OsrmService osrmService;
    private final UbicacionService ubicacionService;

    public RutaService(RutaRepo rutaRepo, UbicacionRepo ubicacionRepo, OsrmService osrmService, UbicacionService ubicacionService) {
        this.rutaRepo = rutaRepo;
        this.ubicacionRepo = ubicacionRepo;
        this.osrmService = osrmService;
        this.ubicacionService = ubicacionService;
    }

    // ==================== MÉTODOS USADOS POR CONTROLADORES ====================

    @Transactional
    public RutaDTO crearRutaDesdeDto(RutaDTO rutaDto) {
        Ruta ruta = DtoMapper.toRutaEntity(rutaDto);
        Ruta guardado = crearRuta(ruta);
        return DtoMapper.toRutaDto(guardado);
    }

    @Transactional
    public RutaDTO actualizarRutaDesdeDto(RutaDTO rutaDto) {
        Ruta ruta = DtoMapper.toRutaEntity(rutaDto);
        Ruta actualizado = actualizarRuta(ruta);
        return DtoMapper.toRutaDto(actualizado);
    }

    @Transactional(readOnly = true)
    public RutaDTO obtenerRutaDtoPorId(Long id) {
        return DtoMapper.toRutaDto(obtenerRutaPorId(id));
    }

    @Transactional(readOnly = true)
    public Map<Long, RutaDTO> listarRutasDto() {
        Map<Long, RutaDTO> rutaMap = new HashMap<>();
        for (Ruta ruta : rutaRepo.findAll()) {
            rutaMap.put(ruta.getId(), DtoMapper.toRutaDto(ruta));
        }
        return rutaMap;
    }

    @Transactional(readOnly = true)
    public List<RutaDTO> obtenerRutasParaAsignarDto(SolicitudTraslado solicitud) {
        List<Ruta> rutas = obtenerRutasParaAsignarASolicitud(solicitud);
        if (rutas == null) {
            return java.util.Collections.emptyList();
        }
        return rutas.stream().map(DtoMapper::toRutaDto).collect(Collectors.toList());
    }

    // ==================== MÉTODOS CON LÓGICA DE NEGOCIO ====================

    @Transactional
    public Ruta crearRuta(Ruta ruta) {

        Map<String,Object> distancia = osrmService.getDistanceDuration(
                ruta.getOrigen().getLatitud(),
                ruta.getOrigen().getLongitud(),
                ruta.getDestino().getLatitud(),
                ruta.getDestino().getLongitud());
        ruta.setDistancia(extraerDistanciaOsrm(distancia));
        
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

    @Transactional(readOnly = true)
    public List<Ruta> obtenerRutasParaAsignarASolicitud(SolicitudTraslado solicitudTraslado) {
        List<Ruta> rutas = this.crearRutasParaSolicitud(solicitudTraslado);
        if (rutas == null || rutas.isEmpty()) return rutas;

        // Obtener tarifa y contenedor asociados a la solicitud (si están disponibles)
        com.SolicitudTraslado.domain.Tarifa tarifa = null;
        com.SolicitudTraslado.domain.Contenedor cont = null;
        if (solicitudTraslado != null) {
            tarifa = solicitudTraslado.getTarifa();
            cont = solicitudTraslado.getContenedor();
        }

        for (Ruta ruta : rutas) {
            try {
                Map<String,Object> osrm = osrmService.getDistanceDuration(ruta.getOrigen().getLatitud(), ruta.getOrigen().getLongitud(), ruta.getDestino().getLatitud(), ruta.getDestino().getLongitud());
                Double distanceMeters = osrm != null ? (Double) osrm.get("distanceMeters") : null;
                Double durationSeconds = osrm != null ? (Double) osrm.get("durationSeconds") : null;

                if (distanceMeters != null) {
                    ruta.setDistancia(distanceMeters);
                }
                if (durationSeconds != null) {
                    ruta.setTiempoEstimado(durationSeconds / 3600.0); // horas
                }

                if (tarifa != null && cont != null && distanceMeters != null) {
                    // Mantener consistencia con cálculo en SolicitudTraslado: aplicar sobre metros como en código existente.
                    Double costo = tarifa.getCostoPorKm() * distanceMeters + tarifa.getCostoPorM3() * cont.getVolumen() + tarifa.getCostoDeCombustible() * distanceMeters;
                    ruta.setCostoEstimado(costo);
                }
            } catch (Exception ex) {
                // No queremos romper la generación de rutas por un fallo en OSRM; registrar y seguir
                // (no inyectamos logger en este Service para no romper constructor)
            }
        }

        return rutas;
    }

    public List<Ruta> crearRutasParaSolicitud(SolicitudTraslado solicitud) {
        if (solicitud == null) throw new IllegalArgumentException("Solicitud no puede ser null");
        Ubicacion origen = solicitud.getUbicacionOrigen();
        Ubicacion destino = solicitud.getUbicacionDestino();
        if (origen == null || destino == null) throw new IllegalArgumentException("Origen y destino son obligatorios para generar rutas");

        java.util.List<Ruta> creadas = new java.util.ArrayList<>();

        // 1) Ruta directa (1 tramo)
        Ruta directa = Ruta.builder()
                .origen(origen)
                .destino(destino)
                .cantTramos(1)
                .asignada(false)
                .solicitud(solicitud)
                .build();
        directa = this.crearRuta(directa);
        creadas.add(directa);

        // 2) Intentar generar una alternativa via una ubicacion intermedia (si existe)
        java.util.Map<Long, Ubicacion> todas = ubicacionService.listarUbicaciones();
        Ubicacion intermedia = null;
        for (Ubicacion u : todas.values()) {
            if (!u.getId().equals(origen.getId()) && !u.getId().equals(destino.getId())) {
                intermedia = u;
                break;
            }
        }

        if (intermedia != null) {
            // Ruta alternativa que conceptualmente tiene 2 tramos (origen->intermedia->destino)
            Ruta viaIntermedia = Ruta.builder()
                    .origen(origen)
                    .destino(destino)
                    .cantTramos(2)
                    .asignada(false)
                    .solicitud(solicitud)
                    .build();
            viaIntermedia = this.crearRuta(viaIntermedia);
            creadas.add(viaIntermedia);
        }

        return creadas;
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

    private Double extraerDistanciaOsrm(Map<String, Object> osrmResponse) {
        if (osrmResponse == null || osrmResponse.isEmpty()) {
            throw new IllegalStateException("No se obtuvo respuesta del servicio OSRM para la ruta");
        }
        if (osrmResponse.containsKey("error")) {
            throw new IllegalStateException("Error devuelto por OSRM al calcular la ruta: " + osrmResponse.get("error"));
        }
        Object value = osrmResponse.get("distanceMeters");
        if (!(value instanceof Number)) {
            throw new IllegalStateException("Respuesta de OSRM inválida. Distancia no presente.");
        }
        double distance = ((Number) value).doubleValue();
        if (distance <= 0) {
            throw new IllegalStateException("OSRM devolvió una distancia no válida: " + distance);
        }
        return distance;
    }
}
