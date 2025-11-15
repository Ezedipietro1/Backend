package com.SolicitudTraslado.services;

import com.SolicitudTraslado.domain.SolicitudTraslado;
import com.SolicitudTraslado.domain.Ubicacion;
import com.SolicitudTraslado.domain.Cliente;
import com.SolicitudTraslado.domain.Contenedor;
import com.SolicitudTraslado.domain.enums.EstadoSolicitud;
import com.SolicitudTraslado.repo.SolicitudTrasladoRepo;
import com.SolicitudTraslado.repo.RutaRepo;
import com.SolicitudTraslado.services.ContenedorService;
import com.SolicitudTraslado.repo.TarifaRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

@Service
public class SolicitudTrasladoService {
    private final SolicitudTrasladoRepo solicitudTrasladoRepo;
    private final RutaRepo rutaRepo;
    private final ContenedorService contenedorService;
    private final TarifaRepo tarifaRepo;

    public SolicitudTrasladoService(SolicitudTrasladoRepo solicitudTrasladoRepo, RutaRepo rutaRepo, ContenedorService contenedorService, TarifaRepo tarifaRepo) {
        this.solicitudTrasladoRepo = solicitudTrasladoRepo;
        this.rutaRepo = rutaRepo;
        this.contenedorService = contenedorService;
        this.tarifaRepo = tarifaRepo;
    }

    @Transactional
    public SolicitudTraslado crearSolicitudTraslado(Cliente cliente, Contenedor contenedor, Ubicacion ubicacionOrigen, Ubicacion ubicacionDestino) {
        Contenedor contenedorNuevo = contenedorService.crearContenedor(contenedor);

        Cliente clienteNuevo = registrarClienteSiNecesario(null);
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
    private final org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();

    private void registrarContenedorSiNecesario(SolicitudTraslado s) {
        if (s.getContenedor() == null) {
            throw new IllegalArgumentException("La solicitud debe incluir un contenedor");
        }
        if (s.getContenedor().getId() == null) {
            // crear contenedor mediante ContenedorService y asignar el id generado
            contenedorService.crearContenedor(s.getContenedor());
        } else {
            // si trae id, verificar que exista
            contenedorService.obtenerContenedorPorId(s.getContenedor().getId());
        }
    }

    private void registrarClienteSiNecesario(SolicitudTraslado s) {
        // Si ya viene clienteId, intentamos verificar existencia remota
        if (s.getClienteId() != null) {
            try {
                String url = "http://localhost:8081/api/clientes/" + s.getClienteId();
                restTemplate.getForObject(url, Cliente.class);
                return; // existe
            } catch (org.springframework.web.client.HttpClientErrorException.NotFound nf) {
                // no existe -> se solicitará creación si se proveen datos
            } catch (Exception ex) {
                // si el servicio no responde, no bloqueamos la creación local
                return;
            }
        }

        // Si no hay clienteId, intentar registrar usando el objeto transitorio `cliente` incluido en la solicitud
        Cliente c = s.getCliente();
        if (s.getClienteId() == null) {
            if (c == null) {
                throw new IllegalArgumentException("Cliente no encontrado. Proporcione 'clienteId' válido o los datos de cliente para crearlo.");
            }
            try {
                String url = "http://localhost:8081/api/clientes";
                Cliente creado = restTemplate.postForObject(url, c, Cliente.class);
                if (creado != null && creado.getId() != null) {
                    s.setClienteId(creado.getId());
                } else {
                    throw new IllegalStateException("No se obtuvo id del cliente creado");
                }
            } catch (Exception ex) {
                throw new IllegalStateException("No se pudo registrar el cliente remoto: " + ex.getMessage(), ex);
            }
        }
    }
    
}
