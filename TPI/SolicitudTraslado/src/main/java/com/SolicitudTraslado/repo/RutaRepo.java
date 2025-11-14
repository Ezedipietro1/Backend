package com.SolicitudTraslado.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SolicitudTraslado.domain.Ruta;

@Repository
public interface RutaRepo extends JpaRepository<Ruta, Long> {
    List<Ruta> findByAsignada(Boolean asignada);

    List<Ruta> findBySolicitud(Long solicitudId);

    List<Ruta> findByOrigenIdAndDestinoId(Long origenId, Long destinoId);
}
