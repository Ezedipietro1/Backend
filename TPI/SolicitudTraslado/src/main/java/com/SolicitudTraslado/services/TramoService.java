package com.SolicitudTraslado.services;

import com.SolicitudTraslado.domain.Tramos;
import com.SolicitudTraslado.domain.enums.TipoTramo;
import com.SolicitudTraslado.repo.TramoRepo;
import com.SolicitudTraslado.repo.CamionRepo;
import com.SolicitudTraslado.repo.UbicacionRepo;
import com.SolicitudTraslado.repo.RutaRepo;
import java.util.Objects;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.HashMap;
import java.sql.Date;

@Service
public class TramoService {
    private final TramoRepo tramoRepo;
    private final CamionRepo camionRepo;
    private final UbicacionRepo ubicacionRepo;
    private final RutaRepo rutaRepo;

    public TramoService(TramoRepo tramoRepo, CamionRepo camionRepo, UbicacionRepo ubicacionRepo, RutaRepo rutaRepo) {
        this.tramoRepo = tramoRepo;
        this.camionRepo = camionRepo;
        this.ubicacionRepo = ubicacionRepo;
        this.rutaRepo = rutaRepo;
    }

    @Transactional
    public Tramos crearTramo(Tramos tramo) {
        verificarTramo(tramo);
        return tramoRepo.save(tramo);
    }

    @Transactional
    public Tramos actualizarTramo(Tramos tramoActualizado) {
        verificarTramo(tramoActualizado);
        return tramoRepo.save(tramoActualizado);
    }

    @Transactional 
    public Tramos actualizarFechaFin(Long tramoId, Date nuevaFechaFin) {
        Tramos tramo = tramoRepo.findById(tramoId)
                .orElseThrow(() -> new IllegalArgumentException("Tramo no encontrado con ID: " + tramoId));

        if (nuevaFechaFin != null && nuevaFechaFin.before(tramo.getFechaInicio())) {
            throw new IllegalArgumentException("La nueva fecha fin no puede ser anterior a la fecha de inicio");
        }

        tramo.setFechaFin(nuevaFechaFin);
        return tramoRepo.save(tramo);
    }

    @Transactional(readOnly = true)
    public Tramos obtenerTramoPorId(Long id) {
        return tramoRepo.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Tramos> obtenerTramosPorTipoTramo(TipoTramo tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("TipoTramo no puede ser null");
        }
        return tramoRepo.findByTipoTramo(tipo);
    }

    @Transactional(readOnly = true)
    public Map<Long, Tramos> listarTramos() {
        Map<Long, Tramos> tramoMap = new HashMap<>();
        for (Tramos tramo : tramoRepo.findAll()) {
            tramoMap.put(tramo.getId(), tramo);
        }
        return tramoMap;
    }

    @Transactional(readOnly = true)
    public List<Tramos> obtenerTramosPorCamionDominio(String dominio) {
        if (dominio == null || dominio.trim().isEmpty()) {
            throw new IllegalArgumentException("Dominio del camión no puede ser null o vacío");
        }
        return tramoRepo.findByCamionDominio(dominio);
    }

    // Validaciones para Tramos
    private void verificarTramo(Tramos t) {
        if (t == null) {
            throw new IllegalArgumentException("Tramo no puede ser null");
        }

        // Camion: obligatorio y debe existir
        if (t.getCamion() == null || t.getCamion().getDominio() == null || t.getCamion().getDominio().trim().isEmpty()) {
            throw new IllegalArgumentException("El camión del tramo es obligatorio y debe tener dominio");
        }
        String dominio = t.getCamion().getDominio();
        camionRepo.findById(dominio).orElseThrow(() -> new IllegalArgumentException("Camión no encontrado con dominio: " + dominio));

        // Origen y destino: obligatorios y deben existir
        if (t.getOrigen() == null || t.getOrigen().getId() == null) {
            throw new IllegalArgumentException("El origen del tramo es obligatorio");
        }
        ubicacionRepo.findById(Objects.requireNonNull(t.getOrigen().getId())).orElseThrow(() -> new IllegalArgumentException("Ubicación origen no encontrada"));

        if (t.getDestino() == null || t.getDestino().getId() == null) {
            throw new IllegalArgumentException("El destino del tramo es obligatorio");
        }
        ubicacionRepo.findById(Objects.requireNonNull(t.getDestino().getId())).orElseThrow(() -> new IllegalArgumentException("Ubicación destino no encontrada"));

        // Fechas: fechaInicio obligatorio; si fechaFin existe debe ser posterior o igual
        if (t.getFechaInicio() == null) {
            throw new IllegalArgumentException("La fecha de inicio es obligatoria");
        }
        if (t.getFechaFin() != null) {
            java.sql.Date inicio = t.getFechaInicio();
            java.sql.Date fin = t.getFechaFin();
            if (fin.before(inicio)) {
                throw new IllegalArgumentException("La fecha fin no puede ser anterior a la fecha de inicio");
            }
        }

        // Ruta: obligatoria y debe existir
        if (t.getRuta() == null || t.getRuta().getId() == null) {
            throw new IllegalArgumentException("La ruta del tramo es obligatoria");
        }
        rutaRepo.findById(Objects.requireNonNull(t.getRuta().getId())).orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada"));

        // Tipo y estado: obligatorios
        if (t.getTipoTramo() == null) {
            throw new IllegalArgumentException("El tipo de tramo es obligatorio");
        }
        if (t.getEstadoTramo() == null) {
            throw new IllegalArgumentException("El estado del tramo es obligatorio");
        }
    }

    
    
}
