package com.SolicitudTraslado.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SolicitudTraslado.domain.Ruta;

@Repository
public interface RutaDepo extends JpaRepository<Ruta, Long> {
    List<RutaDepo> findByAsignadaTrue();

    List<RutaDepo> findBySolicitud(Long solicitudId);

    List<RutaDepo> findByOandD(Long origenId, Long destinoId);

}
