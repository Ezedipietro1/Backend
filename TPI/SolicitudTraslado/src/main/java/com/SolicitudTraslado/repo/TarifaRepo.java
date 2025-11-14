package com.SolicitudTraslado.repo;

import com.SolicitudTraslado.domain.Tarifa;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface TarifaRepo extends JpaRepository<Tarifa, Long> {
    List<Tarifa> findByNumeroSolicitud(Long numeroSolicitud);
}
