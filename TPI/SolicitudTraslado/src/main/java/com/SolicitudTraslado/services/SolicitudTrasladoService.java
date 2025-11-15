package com.SolicitudTraslado.services;

import com.SolicitudTraslado.domain.SolicitudTraslado;
import com.SolicitudTraslado.domain.enums.EstadoSolicitud;
import com.SolicitudTraslado.repo.SolicitudTrasladoRepo;

import com.SolicitudTraslado.domain.Ubicacion;
import com.SolicitudTraslado.services.UbicacionService;

import com.SolicitudTraslado.domain.Cliente;

import com.SolicitudTraslado.domain.Contenedor;
import com.SolicitudTraslado.services.ContenedorService;

import com.SolicitudTraslado.repo.RutaRepo;
import com.SolicitudTraslado.domain.Ruta;
import com.SolicitudTraslado.services.RutaService;

import com.SolicitudTraslado.domain.Tarifa;
import com.SolicitudTraslado.services.TarifaService;
import com.SolicitudTraslado.repo.TarifaRepo;

import com.SolicitudTraslado.services.DistanciaService;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

@Service
public class SolicitudTrasladoService {
    private static final Logger log = LoggerFactory.getLogger(SolicitudTrasladoService.class);
    private final SolicitudTrasladoRepo solicitudTrasladoRepo;
    private final RutaRepo rutaRepo;
    private final RutaService rutaService;
    private final ContenedorService contenedorService;
    private final TarifaService tarifaService;
    private final TarifaRepo tarifaRepo;
    private final UbicacionService ubicacionService;
    private final DistanciaService distanciaService;

    public SolicitudTrasladoService(SolicitudTrasladoRepo solicitudTrasladoRepo, RutaRepo rutaRepo, RutaService rutaService, ContenedorService contenedorService, TarifaRepo tarifaRepo, TarifaService tarifaService, UbicacionService ubicacionService, DistanciaService distanciaService) {
        this.solicitudTrasladoRepo = solicitudTrasladoRepo;
        this.rutaRepo = rutaRepo;
        this.rutaService = rutaService;
        this.contenedorService = contenedorService;
        this.tarifaRepo = tarifaRepo;
        this.tarifaService = tarifaService;
        this.ubicacionService = ubicacionService;
        this.distanciaService = distanciaService;
    }

    @Transactional
    public SolicitudTraslado crearSolicitudTraslado(Long clienteId, String nombre, String apellido, String telefono, boolean activo, String email, Double volumen, Double peso, Long ubicacionOrigenId, Long ubicacionDestinoId) {
        log.info("crearSolicitudTraslado params -> clienteId={}, email={}, volumen={}, peso={}, origenId={}, destinoId={}", clienteId, email, volumen, peso, ubicacionOrigenId, ubicacionDestinoId);

        Contenedor contenedorNuevo = new Contenedor();
        contenedorNuevo.setVolumen(volumen);
        contenedorNuevo.setPeso(peso);
        contenedorNuevo = contenedorService.crearContenedor(contenedorNuevo);

        Cliente clienteNuevo = registrarClienteSiNecesario(clienteId, nombre, apellido, telefono, activo, email);

        Ubicacion origenCreada = ubicacionService.obtenerUbicacionPorId(ubicacionOrigenId);
        Ubicacion destinoCreada = ubicacionService.obtenerUbicacionPorId(ubicacionDestinoId);

        Tarifa tarifaDefault = tarifaService.obtenerTarifa();
        if (tarifaDefault == null) {
            throw new IllegalArgumentException("No se encontró una tarifa por defecto");
        }

        EstadoSolicitud estadoInicial = EstadoSolicitud.BORRADOR;

        Map<String,Object> distancia = distanciaService.calcularDistancia(origenCreada.getLatitud(), origenCreada.getLongitud(), destinoCreada.getLatitud(), destinoCreada.getLongitud());

        Double costoEstimado = tarifaDefault.getCostoPorKm() * (Double) distancia.get("distanceKm") + tarifaDefault.getCostoPorM3() * contenedorNuevo.getVolumen() + tarifaDefault.getCostoDeCombustible() * 1.0 * (Double) distancia.get("distanceKm");

        Double tiempoEstimado = (Double) distancia.get("distanceKm") / 80.0; // asumiendo velocidad promedio de 60 km/h

        SolicitudTraslado nuevaSolicitud = SolicitudTraslado.builder()
                .clienteId(clienteNuevo.getId())
                .contenedor(contenedorNuevo)
                .tarifa(tarifaDefault)
                .ubicacionOrigen(origenCreada)
                .ubicacionDestino(destinoCreada)
                .costoEstimado(costoEstimado)
                .tiempoEstimado(tiempoEstimado)
                .estado(estadoInicial)
                .build();
        return solicitudTrasladoRepo.save(nuevaSolicitud);
    }

    private List<Ruta> crearRutasParaSolicitud(SolicitudTraslado solicitud) {
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
        directa = rutaService.crearRuta(directa);
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
            viaIntermedia = rutaService.crearRuta(viaIntermedia);
            creadas.add(viaIntermedia);
        }

        return creadas;
    }

    @Transactional
    public SolicitudTraslado actualizarSolicitudTraslado(SolicitudTraslado solicitudActualizada) {
        validarSolicitud(solicitudActualizada);
        return solicitudTrasladoRepo.save(solicitudActualizada);
    }

    @Transactional(readOnly = true)
    public Map<Long, SolicitudTraslado> listarSolicitudes() {
        Map<Long, SolicitudTraslado> solicitudMap = new HashMap<>();
        for (SolicitudTraslado solicitud : solicitudTrasladoRepo.findAll()) {
            solicitudMap.put(solicitud.getNumero(), solicitud);
        }
        return solicitudMap;
    }

    @Transactional(readOnly = true)
    public SolicitudTraslado obtenerSolicitudPorNumero(Long numero) {
        return solicitudTrasladoRepo.findById(numero)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada con número: " + numero));
    }

    @Transactional(readOnly = true)
    public Map<Long, SolicitudTraslado> obtenerSolicitudesPorEstado(EstadoSolicitud estado) {
        List<SolicitudTraslado> solicitudes = solicitudTrasladoRepo.findByEstado(estado);
        Map<Long, SolicitudTraslado> solicitudMap = new HashMap<>();
        for (SolicitudTraslado solicitud : solicitudes) {
            solicitudMap.put(solicitud.getNumero(), solicitud);
        }
        return solicitudMap;
    }

    @Transactional(readOnly = true)
    public List<SolicitudTraslado> obtenerSolicitudesPorClienteId(Long clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("El clienteId no puede ser null");
        }
        return solicitudTrasladoRepo.findByClienteId(clienteId);
    }
    
    // Validaciones para SolicitudTraslado
    private void validarSolicitud(SolicitudTraslado s) {
        if (s == null) {
            throw new IllegalArgumentException("Solicitud no puede ser null");
        }

        // ClienteId: obligatorio
        if (s.getClienteId() == null) {
            throw new IllegalArgumentException("El clienteId de la solicitud es obligatorio");
        }

        // Ruta: obligatorio y debe existir
        if (s.getRuta() == null || s.getRuta().getId() == null) {
            throw new IllegalArgumentException("La ruta de la solicitud es obligatoria");
        }
        rutaRepo.findById(Objects.requireNonNull(s.getRuta().getId())).orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada"));

        // Contenedor: obligatorio y debe existir (registro previo por ContenedorService)
        if (s.getContenedor() == null || s.getContenedor().getId() == null) {
            throw new IllegalArgumentException("El contenedor de la solicitud es obligatorio");
        }
        contenedorService.obtenerContenedorPorId(Objects.requireNonNull(s.getContenedor().getId()));

        // Tarifa: obligatorio y debe existir
        if (s.getTarifa() == null || s.getTarifa().getId() == null) {
            throw new IllegalArgumentException("La tarifa de la solicitud es obligatoria");
        }
        tarifaRepo.findById(Objects.requireNonNull(s.getTarifa().getId())).orElseThrow(() -> new IllegalArgumentException("Tarifa no encontrada"));

        // Costos/tiempos estimados: obligatorios y positivos
        if (s.getCostoEstimado() == null || s.getCostoEstimado() <= 0) {
            throw new IllegalArgumentException("El costo estimado debe ser un valor positivo");
        }
        if (s.getTiempoEstimado() == null || s.getTiempoEstimado() <= 0) {
            throw new IllegalArgumentException("El tiempo estimado debe ser un valor positivo");
        }

        // Si existe costoFinal o tiempoReal deben ser no negativos
        if (s.getCostoFinal() != null && s.getCostoFinal() < 0) {
            throw new IllegalArgumentException("El costo final no puede ser negativo");
        }
        if (s.getTiempoReal() != null && s.getTiempoReal() < 0) {
            throw new IllegalArgumentException("El tiempo real no puede ser negativo");
        }

        // Estado: obligatorio
        if (s.getEstado() == null) {
            throw new IllegalArgumentException("El estado de la solicitud es obligatorio");
        }
    }
    
    // --- Helpers for create flow ---
    @Value("${Cliente.service.url:http://localhost:8081}")
    private String clienteServiceUrl;

    private final org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();

    public Cliente buscarClientePorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del cliente no puede ser null");
        }
        try {
            String url = clienteServiceUrl + "/api/clientes/" + id;
            Cliente c = restTemplate.getForObject(url, Cliente.class);
            if (c == null || c.getId() == null) {
                return null;
            }
            return c;
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound nf) {
            // no existe
            return null;
        } catch (org.springframework.web.client.ResourceAccessException rae) {
            throw new IllegalStateException("Servicio de Clientes no disponible: " + rae.getMessage(), rae);
        } catch (Exception ex) {
            throw new IllegalStateException("Error buscando cliente remoto: " + ex.getMessage(), ex);
        }
    }

    private Cliente registrarClienteSiNecesario(Long clienteId, String nombre, String apellido, String telefono, boolean activo, String email) {
        // Si se envió un id, verificamos existencia remota primero
        if (clienteId != null) {
            try {
                String url = clienteServiceUrl + "/api/clientes/" + clienteId;
                Cliente encontrado = restTemplate.getForObject(url, Cliente.class);
                if (encontrado != null && encontrado.getId() != null) {
                    return encontrado;
                }
                // si devuelve body vacío o sin id, seguimos a creación
            } catch (org.springframework.web.client.HttpClientErrorException.NotFound nf) {
                // no existe -> intentaremos crear con los datos provistos
            } catch (org.springframework.web.client.ResourceAccessException rae) {
                throw new IllegalStateException("Servicio de Clientes no disponible: " + rae.getMessage(), rae);
            } catch (Exception ex) {
                throw new IllegalStateException("Error verificando cliente remoto: " + ex.getMessage(), ex);
            }
        }

        // Construir payload para creación con campos provistos
        try {
            String url = clienteServiceUrl + "/api/clientes";
            java.util.Map<String, Object> payload = new java.util.HashMap<>();
            if (nombre != null) payload.put("nombre", nombre);
            if (apellido != null) payload.put("apellido", apellido);
            if (telefono != null) payload.put("telefono", telefono);
            payload.put("estado", activo);

            // Usar email provisto o generar uno por defecto
            String finalEmail = email;
            if (finalEmail == null || finalEmail.isBlank()) {
                String base = "user";
                if (nombre != null && apellido != null) {
                    base = nombre.toLowerCase().replaceAll("\\s+", "") + "." + apellido.toLowerCase().replaceAll("\\s+", "");
                } else if (nombre != null) {
                    base = nombre.toLowerCase().replaceAll("\\s+", "");
                }
                finalEmail = base + "@example.local";
            }
            payload.put("email", finalEmail);

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/json");
            org.springframework.http.HttpEntity<java.util.Map<String, Object>> entity = new org.springframework.http.HttpEntity<>(payload, headers);

            org.springframework.http.ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                String body = response.getBody();
                Long newId = null;
                if (body != null && !body.isBlank()) {
                    try {
                        com.fasterxml.jackson.databind.JsonNode node = new com.fasterxml.jackson.databind.ObjectMapper().readTree(body);
                        if (node.has("id") && !node.get("id").isNull()) newId = node.get("id").asLong();
                        else if (node.has("idCliente") && !node.get("idCliente").isNull()) newId = node.get("idCliente").asLong();
                        else if (node.has("clienteId") && !node.get("clienteId").isNull()) newId = node.get("clienteId").asLong();
                    } catch (Exception e) {
                        // ignore parse error, try headers next
                    }
                }

                // If body didn't contain id, try Location header
                if (newId == null) {
                    java.net.URI loc = response.getHeaders().getLocation();
                    if (loc != null) {
                        String path = loc.getPath();
                        String[] parts = path.split("/");
                        String last = parts[parts.length - 1];
                        try {
                            newId = Long.parseLong(last);
                        } catch (NumberFormatException nfe) {
                            // ignore
                        }
                    }
                }

                if (newId != null) {
                    Cliente created = new Cliente();
                    created.setId(newId);
                    created.setNombre(nombre);
                    created.setApellido(apellido);
                    created.setTelefono(telefono);
                    created.setActivo(activo);
                    created.setEmail((String) payload.get("email"));
                    return created;
                }
            }
            int status = (response != null && response.getStatusCode() != null) ? response.getStatusCode().value() : 0;
            throw new IllegalStateException("No se obtuvo id del cliente creado por el servicio remoto; status=" + status);
        } catch (org.springframework.web.client.ResourceAccessException rae) {
            throw new IllegalStateException("Servicio de Clientes no disponible al intentar crear cliente: " + rae.getMessage(), rae);
        } catch (org.springframework.web.client.HttpClientErrorException hce) {
            throw new IllegalStateException("Error de cliente al crear cliente remoto: " + hce.getStatusCode() + " - " + hce.getResponseBodyAsString(), hce);
            } catch (Exception ex) {
                throw new IllegalStateException("No se pudo registrar el cliente remoto: " + ex.getMessage(), ex);
            }
    }
}
